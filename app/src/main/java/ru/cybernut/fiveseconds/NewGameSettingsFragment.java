package ru.cybernut.fiveseconds;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import ru.cybernut.fiveseconds.model.GameEngine;
import ru.cybernut.fiveseconds.model.PlayersList;
import ru.cybernut.fiveseconds.model.QuestionSet;
import ru.cybernut.fiveseconds.model.QuestionSetList;
import ru.cybernut.fiveseconds.view.QuestionSetModel;

public class NewGameSettingsFragment extends Fragment {

    private static final String TAG = "NewGameSettingsFragment";

    private static final int NETWORK_STATE_NONE = 0;
    private static final int NETWORK_STATE_WIFI = 1;
    private static final int NETWORK_STATE_MOBILE = 2;

    private int numberOfPlayers = 2;
    private SeekBar numberOfRoundsSeekBar;
    private TextView numberOfRoundsTextView;
    private ImageButton startNewGameButton;
    private OnGamePreparedListener onGamePreparedListener;
    private RecyclerView questionSetsRecyclerView;
    private QuestionSetAdapter questionSetAdapter;
    private DownloadTask downloadTask;
    private Spinner gameTypeSpinner;
    private ProgressDialog mProgressDialog;
    private List<QuestionSetModel> questionSetModelList;
    private TextView addTimeValueTextView;

    public static NewGameSettingsFragment newInstance() {
        return new NewGameSettingsFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareQuestionSetModelList();
        if (questionSetAdapter != null){
            questionSetAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onGamePreparedListener = (OnGamePreparedListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString() + " must implement OnGamePreparedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_game_settings_fragment, container, false);

        numberOfRoundsTextView = v.findViewById(R.id.numberOfRounds);
        numberOfPlayers = PlayersList.getInstance().getNumberOfPlayers();

        gameTypeSpinner = v.findViewById(R.id.game_type_spinner);
        gameTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 1) {
                    addTimeValueTextView.setVisibility(View.VISIBLE);
                } else {
                    addTimeValueTextView.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        startNewGameButton = v.findViewById(R.id.startNewGameButton);
        startNewGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numberOfRounds = numberOfRoundsSeekBar.getProgress() + FiveSecondsApplication.MIN_QUANTITY_OF_ROUNDS;
                if (numberOfPlayers < 2) {
                    Toast.makeText(getActivity(), R.string.incorrect_number_of_rounds, Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<Integer> setIds = getCheckedSets();
                    if (setIds.size() > 0) {
                        if (gameTypeSpinner.getSelectedItemPosition() == GameEngine.GAME_TYPE_AUTO_PLAY_SOUND && !checkAvailabilitySounds()) {
                            return;
                        }
                        onGamePreparedListener.onGamePrepared(numberOfRounds, setIds, gameTypeSpinner.getSelectedItemPosition());
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.incorrect_questionsets), Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });

        questionSetsRecyclerView = v.findViewById(R.id.question_sets_recycler_view);
        questionSetsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        questionSetAdapter = new QuestionSetAdapter();
        questionSetsRecyclerView.setAdapter(questionSetAdapter);

        numberOfRoundsSeekBar = v.findViewById(R.id.numberOfRoundsSeekBar);
        numberOfRoundsSeekBar.setMax(FiveSecondsApplication.MAX_QUANTITY_OF_ROUNDS - FiveSecondsApplication.MIN_QUANTITY_OF_ROUNDS);
        //numberOfRoundsSeekBar.setProgress(numberOfPlayers);
        updateNumberOfQuestionsTextView();
        numberOfRoundsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateNumberOfQuestionsTextView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateNumberOfQuestionsTextView();
            }
        });

        mProgressDialog = new ProgressDialog(getActivity(), R.style.DialogTheme);
        mProgressDialog.setMessage(getResources().getString(R.string.sound_downloading_progressbar_title));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true); //cancel the task
            }
        });

        addTimeValueTextView = v.findViewById(R.id.additional_time_value_textview);

        initializeSettings();

        return v;
    }

    private void initializeSettings() {
        Context context = getActivity();
        if (context != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            //Default game type
            int gameType = sharedPreferences.getInt(FiveSecondsApplication.PREF_DEFAULT_GAME_TYPE, 0);
            gameTypeSpinner.setSelection(gameType);
            //additional time
            int addTime = sharedPreferences.getInt(FiveSecondsApplication.PREF_ADD_TIME_VALUE, FiveSecondsApplication.DEFAULT_ADDITIONAL_TIME_VALUE);
            addTimeValueTextView.setText(String.valueOf(addTime) + " " + getResources().getString(R.string.seconds));
            //default number of rounds
            int numberOfRounds = sharedPreferences.getInt(FiveSecondsApplication.PREF_DEFAULT_NUMBER_OF_ROUNDS, FiveSecondsApplication.DEFAULT_NUMBER_OF_ROUNDS);
            numberOfRoundsSeekBar.setProgress(numberOfRounds - FiveSecondsApplication.MIN_QUANTITY_OF_ROUNDS);
            updateNumberOfQuestionsTextView();
        }
    }

    private boolean checkAvailabilitySounds() {
        //TODO: need good check
        for (QuestionSetModel questionSetModel : questionSetModelList) {
            if (questionSetModel.isChecked() && !questionSetModel.isSoundsAvailable()) {
                Toast.makeText(getActivity(), String.format(getString(R.string.sound_pack_is_not_available).toString(), questionSetModel.getName()), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    //TODO: need change this method on the stream
    private ArrayList<Integer> getCheckedSets() {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < questionSetModelList.size(); i++) {
            QuestionSetModel temp = questionSetModelList.get(i);
            if(temp.isChecked()) {
                list.add(i + 1);
            }
        }
        return list;
    }

    private void prepareQuestionSetModelList() {
        List <QuestionSet> list = QuestionSetList.getInstance().getQuestionSets();
        questionSetModelList = new ArrayList<>();
        for (QuestionSet questionSet : list) {
            questionSetModelList.add(new QuestionSetModel(questionSet));
        }
    }

    public interface OnGamePreparedListener {
        void onGamePrepared(int numberOfRounds, ArrayList<Integer> setIds, int gameType);
    }

    private void updateNumberOfQuestionsTextView() {
        numberOfRoundsTextView.setText(String.valueOf(FiveSecondsApplication.MIN_QUANTITY_OF_ROUNDS + numberOfRoundsSeekBar.getProgress()));
    }

    public void downloadSounds(QuestionSetModel questionSetModel) {
        downloadTask = new DownloadTask(getActivity());
        downloadTask.execute(questionSetModel);
    }

    private int checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if(networkInfo.getType() == connectivityManager.TYPE_WIFI) {
                return NETWORK_STATE_WIFI;
            } else if(networkInfo.getType() == connectivityManager.TYPE_MOBILE) {
                return NETWORK_STATE_MOBILE;
            }
        }
        return NETWORK_STATE_NONE;
    }

    private void openConfirmUsingMobileNetworkDialog(final QuestionSetModel questionSetModelForLoading) {

        AlertDialog.Builder confirmMobileNetworkDialog = new AlertDialog.Builder(
                getActivity(), R.style.DialogTheme);
        confirmMobileNetworkDialog.setTitle(R.string.mobile_network_using_dialog_title);

        confirmMobileNetworkDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadSounds(questionSetModelForLoading);
            }
        });

        confirmMobileNetworkDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        confirmMobileNetworkDialog.show();
    }

    private class QuestionSetHolder extends RecyclerView.ViewHolder {

        private QuestionSetModel questionSetModel;
        private CheckBox questionSetNameCheckBox;
        private Button loadSoundsButton;
        private Button buyQuestionsSetButton;

        public QuestionSetHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.question_set_list_item, parent, false));

            loadSoundsButton = itemView.findViewById(R.id.load_sounds_button);
            loadSoundsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if(!questionSetModel.isAvailable()) {
//                        Intent intent = new Intent(getContext(), ShopActivity.class);
//                        startActivity(intent);
//                        return;
//                    }

                    int networkType = checkNetworkConnection();
                    switch (networkType) {
                        case NETWORK_STATE_NONE:
                            Toast.makeText(getActivity(), R.string.internet_is_not_available, Toast.LENGTH_LONG).show();
                            break;
                        case NETWORK_STATE_WIFI:
                            downloadSounds(questionSetModel);
                            break;
                        case NETWORK_STATE_MOBILE:
                            openConfirmUsingMobileNetworkDialog(questionSetModel);
                            break;
                    }
                }
            });

            buyQuestionsSetButton = itemView.findViewById(R.id.buy_question_set_button);
            buyQuestionsSetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ShopActivity.class);
                    startActivity(intent);
                }
            });
            questionSetNameCheckBox = itemView.findViewById(R.id.question_set_name_checkbox);
            questionSetNameCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                   @Override
                   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        questionSetModel.setChecked(isChecked);
                   }
               }
            );
        }

        public void bind(QuestionSetModel questionSetModel) {
            this.questionSetModel = questionSetModel;
            if(this.questionSetModel!= null) {
                questionSetNameCheckBox.setText(this.questionSetModel.getName() + (questionSetModel.isFree() ? "" : " ($)"));
                if(!questionSetModel.isAvailable()) {
                    questionSetNameCheckBox.setEnabled(false);
                    loadSoundsButton.setVisibility(View.GONE);
                    buyQuestionsSetButton.setVisibility(View.VISIBLE);
                } else {
                    questionSetNameCheckBox.setEnabled(true);
                    loadSoundsButton.setVisibility(View.VISIBLE);
                    buyQuestionsSetButton.setVisibility(View.GONE);
                }
                questionSetNameCheckBox.setChecked(questionSetModel.isChecked());
                loadSoundsButton.setText(getResources().getText(R.string.download_soundspack) + " (" + questionSetModel.getSoundsFilesSize() + ")");
            }
        }
    }

    private class QuestionSetAdapter extends RecyclerView.Adapter<QuestionSetHolder> {

        @Override
        public QuestionSetHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new QuestionSetHolder(inflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull QuestionSetHolder questionSetHolder, int position) {
            QuestionSetModel questionSetModel = questionSetModelList.get(position);
            if(questionSetModel!= null) {
                questionSetHolder.bind(questionSetModel);
            }
        }

        @Override
        public int getItemCount() {
            return questionSetModelList.size();
        }
    }

    // usually, subclasses of AsyncTask are declared inside the activity class.
    // that way, you can easily modify the UI thread from here
    private class DownloadTask extends AsyncTask<QuestionSetModel, Integer, String> {

        private Context context;
        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(QuestionSetModel... questionSetModels) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                QuestionSetModel questionSetModel = questionSetModels[0];
                URL url = new URL(questionSetModel.getSoundsLink());
                Log.i(TAG, "doInBackground: trying to connect");
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }
                Log.i(TAG, "doInBackground: connection OK");

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                String filePath = FiveSecondsApplication.getSoundFolderPath();
                String fileName = "FiveSeconds_sounds.zip";
                Log.i(TAG, "doInBackground: file path:" + filePath);
                File file = new File(filePath);
                if(!file.exists()) {
                    file.mkdirs();
                }
                file = new File(filePath + fileName);
                output = new FileOutputStream(file);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
                ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
                ZipEntry zipEntry;
                //mProgressDialog.setMessage(getString(R.string.sound_unpacking_progressbar_title));
                while ((zipEntry = zipInputStream.getNextEntry())!=null) {
                    FileOutputStream fileOutputStream = new FileOutputStream(filePath + zipEntry.getName());
                    final byte[] bytes = new byte[4096];
                    int length;
                    while ((length = zipInputStream.read(bytes)) >= 0) {
                        fileOutputStream.write(bytes, 0, length);
                    }
                    fileOutputStream.flush();
                    zipInputStream.closeEntry();
                    fileOutputStream.close();
                }
                //TODO: incorrect place for this code
                questionSetModel.markAsDownloaded();
                QuestionSetList.getInstance().updateQuestionSet(questionSetModel.getQuestionSet());

                zipInputStream.close();
                file.delete();
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if(mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            if (result != null)
                Toast.makeText(context,getString(R.string.sound_file_download_error)+result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, R.string.sound_file_downloaded, Toast.LENGTH_SHORT).show();
        }
    }

}
