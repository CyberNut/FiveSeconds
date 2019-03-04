package ru.cybernut.fiveseconds;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipInputStream;

import javax.net.ssl.HttpsURLConnection;

import ru.cybernut.fiveseconds.model.PlayersList;
import ru.cybernut.fiveseconds.model.QuestionSet;
import ru.cybernut.fiveseconds.model.QuestionSetList;


public class NewGameSettingsFragment extends Fragment {

    private static final String TAG = "NewGameSettingsFragment";
    private static final int MAX_QUANTITY_OF_QUESTIONS = 20;
    private static final int MIN_QUANTITY_OF_QUESTIONS = 3;

    private int numberOfPlayers = 2;
    private SeekBar numberOfQuestionsSeekBar;
    private TextView numberOfQuestionsTextView;
    private ImageButton startNewGameButton;
    private OnGamePreparedListener onGamePreparedListener;
    private RecyclerView questionSetsRecyclerView;
    private QuestionSetAdapter questionSetAdapter;
    private ArrayList<Integer> setIds = new ArrayList<>();
    private DownloadTask downloadTask;

    // declare the dialog as a member field of your activity
    ProgressDialog mProgressDialog;

    public static NewGameSettingsFragment newInstance() {
        return new NewGameSettingsFragment();
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

        numberOfQuestionsTextView = (TextView)v.findViewById(R.id.numberOfQuestions);
        numberOfPlayers = PlayersList.getInstance().getNumberOfPlayers();

        startNewGameButton = (ImageButton)v.findViewById(R.id.startNewGameButton);
        startNewGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numberOfQuestions = MIN_QUANTITY_OF_QUESTIONS * numberOfPlayers + numberOfPlayers * numberOfQuestionsSeekBar.getProgress();
                if (numberOfQuestions <= MIN_QUANTITY_OF_QUESTIONS || numberOfPlayers < 2) {
                    Toast.makeText(getActivity(), R.string.incorrect_number_of_questions, Toast.LENGTH_SHORT).show();
                } else {
                    onGamePreparedListener.onGamePrepared(numberOfQuestions,  setIds);
                }
            }
        });

        questionSetsRecyclerView = v.findViewById(R.id.question_sets_recycler_view);
        questionSetsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        questionSetAdapter = new QuestionSetAdapter(QuestionSetList.getInstance(getActivity()).getQuestionSets());
        questionSetsRecyclerView.setAdapter(questionSetAdapter);


        numberOfQuestionsSeekBar = (SeekBar)v.findViewById(R.id.numberOfQuestionsSeekBar);
        numberOfQuestionsSeekBar.setMax(MAX_QUANTITY_OF_QUESTIONS - MIN_QUANTITY_OF_QUESTIONS);
        numberOfQuestionsSeekBar.setProgress(numberOfPlayers);
        updateNumberOfQuestionsTextView();
        numberOfQuestionsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        // instantiate it within the onCreate method
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("A message");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

        // execute this when the downloader must be fired
        downloadTask = new DownloadTask(getActivity());

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true); //cancel the task
            }
        });

        return v;
    }

    public interface OnGamePreparedListener {
        public void onGamePrepared(int numberOfQuestions, ArrayList<Integer> setIds);
    }


    private void updateNumberOfQuestionsTextView() {
        numberOfPlayers = PlayersList.getInstance().getNumberOfPlayers();
        numberOfQuestionsTextView.setText(String.valueOf(MIN_QUANTITY_OF_QUESTIONS * numberOfPlayers +  numberOfPlayers * numberOfQuestionsSeekBar.getProgress()));
    }

    private class QuestionSetHolder extends RecyclerView.ViewHolder {

        private QuestionSet questionSet;
        private CheckBox questionSetNameCheckBox;
        private ImageButton loadSoundsButton;
        private int index;

        public QuestionSetHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.question_set_list_item, parent, false));

            loadSoundsButton = (ImageButton) itemView.findViewById(R.id.load_sounds_button);
            loadSoundsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadTask.execute("https://cloud.mail.ru/public/5k3W/uWvq5Khmy");
                }
            });
            questionSetNameCheckBox = (CheckBox) itemView.findViewById(R.id.question_set_name_checkbox);
            questionSetNameCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                   @Override
                   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                       if (isChecked) {
                           setIds.add(index + 1);
                       } else {
                           setIds.remove(index + 1);
                       }
                   }
               }
            );
        }

        public void bind(QuestionSet questionSet, int index) {
            this.questionSet = questionSet;
            this.index = index;
            if(this.questionSet!= null) {
                questionSetNameCheckBox.setText(this.questionSet.getName());
            }
        }
    }

    private class QuestionSetAdapter extends RecyclerView.Adapter<QuestionSetHolder> {

        private List<QuestionSet> questionSetList;

        public QuestionSetAdapter(List<QuestionSet> questionSetList) {
            this.questionSetList = questionSetList;
        }

//        protected List<String> getSelectedQuestionSets() {
//            String[] stringNames;
//            for (QuestionSet questionSet : questionSetList) {
//
//
//            }
//
//        }

        @Override
        public QuestionSetHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new QuestionSetHolder(inflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull QuestionSetHolder questionSetHolder, int position) {
            QuestionSet questionSet = questionSetList.get(position);
            if(questionSet!= null) {
                questionSetHolder.bind(questionSet, position);
            }
        }

        @Override
        public int getItemCount() {
            return questionSetList.size();
        }
    }


    // usually, subclasses of AsyncTask are declared inside the activity class.
    // that way, you can easily modify the UI thread from here
    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private static final String PATH  = "/Sounds/";
        private String currentLocale;
        public DownloadTask(Context context) {
            this.context = context;
            this.currentLocale = Locale.getDefault().getLanguage();
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                Log.i(TAG, "doInBackground: trying to connect");
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }
                Log.i(TAG, "doInBackground: connection OK");

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                String filePath = context.getExternalFilesDir(null) + PATH + currentLocale + "/";
                String fileName = "test.zip";
                Log.i(TAG, "doInBackground: file path:" + filePath);
                File file = new File(filePath);
                if(!file.exists()) {
                    file.mkdirs();
                    file = new File(filePath + fileName);
                }
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
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
        }
    }
}
