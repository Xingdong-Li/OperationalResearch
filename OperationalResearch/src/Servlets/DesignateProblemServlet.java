package Servlets;

import DesignateProblem.DesignateSolver;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
/**
 * @author  Li Xingdong
 * @since 2020.11.2
 */
@WebServlet("/designateProblemServlet")
public class DesignateProblemServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String list = request.getParameter("list");
        if (list==null){
            response.sendRedirect(request.getContextPath()+"/index.html");
            return;
        }
        String rawData = list.substring(1,list.length()-1);
        String[] split = rawData.split(",");
        List<Integer> dataList=new ArrayList<>();
        for (String str: split) {
            dataList.add(Integer.parseInt(str));
        }
        DesignateSolver designateSolver;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Map<String,Object> data = new HashMap<>();
        try {
            designateSolver = new DesignateSolver(dataList);
            designateSolver.start();
            data.put("status",true);
            data.put("dataList", designateSolver.getIntermediary());
            data.put("answer", designateSolver.getAnswer());
            data.put("ticks", designateSolver.getIntermediaryTicks());
        } catch (Exception e) {
            e.printStackTrace();
            data.put("status",false);
        }
        response.setContentType("application/json;charset=utf-8");
        mapper.writeValue(response.getOutputStream(),data);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(request.getContextPath()+"/index.html");
    }
}
