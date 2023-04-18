package com.exam.controller;

import com.exam.model.exam.Question;
import com.exam.model.exam.Quiz;
import com.exam.service.QuestionService;
import com.exam.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuizService quizService;

    //add question
    @PostMapping("/")
    public ResponseEntity<Question> addQuestion(@RequestBody Question question){
        return ResponseEntity.ok(this.questionService.addQuestion(question));
    }

    //update question
    @PutMapping("/")
    public ResponseEntity<Question> updateQuestion(@RequestBody Question question){
        return  ResponseEntity.ok(this.questionService.updateQuestion(question));
    }

    //get all question of any qid
    @GetMapping("/quiz/{qid}")
    public ResponseEntity<?> getQuestionOfQuiz(@PathVariable("qid") Long qid){
        /* Quiz quiz = new Quiz();
        quiz.setqId(qid);
        Set<Question> questionOfQuiz = this.questionService.getQuestionOfQuiz(quiz);
        return ResponseEntity.ok(questionOfQuiz);*/

        Quiz quiz = this.quizService.getQuiz(qid);
        Set<Question> questions = quiz.getQuestions();
        List<Question> list =  new ArrayList(questions);
        if(list.size() > Integer.parseInt(quiz.getNumberOfQuestions())) {
            list = list.subList(0, Integer.parseInt(quiz.getNumberOfQuestions()+1));
        }
        list.forEach((q) -> {
            q.setAnswer("");
        });
        Collections.shuffle(list);
        return ResponseEntity.ok(list);
    }

    //get all question
    @GetMapping("/quiz/all/{qid}")
    public ResponseEntity<?> getQuestionOfQuizAdmin(@PathVariable("qid") Long qid){
        Quiz quiz = new Quiz();
        quiz.setqId(qid);
        Set<Question> questionsOfQuiz = this.questionService.getQuestionOfQuiz(quiz);
        return ResponseEntity.ok(questionsOfQuiz);
    }

    //get question
    @GetMapping("/{quesId}")
    public Question getQuestion(@PathVariable("quesId") Long quesId){
        return this.questionService.getQuestion(quesId);
    }

    //delete question
    @DeleteMapping("/{quesId}")
    public void deleteQuestion(@PathVariable("quesId") Long quesId){
        this.questionService.deleteQuestion(quesId);
    }

    //eval quiz
    @PostMapping("/evaluation-quiz")
    public  ResponseEntity<?> evalQuiz(@RequestBody List<Question> questions){
        System.out.println(questions);

        double marksGot=0;
        int correctAnswer=0;
        int attempted=0;
        for(Question q:questions){
            //single questions
            Question question = this.questionService.get(q.getQuesId());
            if(question.getAnswer().equals(q.getGiveAnswer())) {
                //correct
                correctAnswer++;
                double marksOfSingle = Double.parseDouble(questions.get(0).getQuiz().getMaxMarks())/questions.size();
                marksGot+=marksOfSingle;
            }
            if(q.getGiveAnswer()!=null) {
                attempted++;
            }
        }
        //Map<String, Object> of=Map.of("marksGot",marksGot,"correctAnswers",correctAnswer,"attempted",attempted);
        Map<Object, Object> of = new HashMap<>();
        of.put("marksGot", marksGot);
        of.put("correctAnswers", correctAnswer);
        of.put("attempted", attempted);

        return ResponseEntity.ok(of);
    }
}
