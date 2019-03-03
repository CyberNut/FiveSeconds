package ru.cybernut.fiveseconds;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.cybernut.fiveseconds.model.PlayersList;
import ru.cybernut.fiveseconds.model.QuestionSet;
import ru.cybernut.fiveseconds.model.QuestionSetList;


public class NewGameSettingsFragment extends Fragment {

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
        private int index;

        public QuestionSetHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.question_set_list_item, parent, false));

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

}
