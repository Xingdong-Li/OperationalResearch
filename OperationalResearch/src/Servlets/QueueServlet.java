package Servlets;

import QueueingTheory.QTConceptsLoader;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@WebServlet("/queueServlet")
public class QueueServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=utf-8");
        Map<String, Object> map = new HashMap<>();
        int number = Integer.parseInt(request.getParameter("number"));
        if (number <= QTConceptsLoader.conceptsMap.size()){
            Set<Object> objects = QTConceptsLoader.conceptsMap.keySet();
            List<Object> keys = new ArrayList<>(objects);
            Collections.shuffle(keys);
            int left = QTConceptsLoader.conceptsMap.size()-number;
            List<Object> problems = new ArrayList<>();
            List<Object> answers = new ArrayList<>();
            if (left==0){
                problems=keys;
            }else if (number>=left){
                while (left>0){
                    keys.remove(keys.size()-1);
                    --left;
                }
                problems=keys;
            }else {
                for (int i = 0; i < number; i++) {
                    problems.add(keys.get(i));
                }
            }
            problems.forEach(e->answers.add(QTConceptsLoader.conceptsMap.getProperty((String) e)));
            map.put("status", true);
            map.put("problems",problems);
            map.put("answers",answers);
            List<Object> choices = new ArrayList<>(QTConceptsLoader.conceptsMap.values());
            choices.add("不知道");
            choices.add("真的想不起来了");
            choices.add("下次一定记住");
            map.put("choices",choices);
        }else {
            map.put("status", false);
            map.put("max", QTConceptsLoader.conceptsMap.size());
        }
        mapper.writeValue(response.getOutputStream(), map);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}
