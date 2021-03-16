package Servlets;

import DynamicProgramming.Dynamic;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @author  Li Xingdong
 * @since 2020.11.12
 */
@WebServlet("/dynamicPlanServlet")
public class DynamicPlanServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String list = request.getParameter("list");
        String assignNum = request.getParameter("assignNum");
        String backward = request.getParameter("backward");
        if (list==null || assignNum==null || backward==null){
            response.sendRedirect(request.getContextPath()+"/index.html");
            return;
        }
        String rawData = list.substring(1,list.length()-1);
        String[] split = rawData.split(",");
        List<Integer> dataList=new ArrayList<>();
        for (String str: split) {
            dataList.add(Integer.parseInt(str));
        }
        Dynamic dynamic = new Dynamic(Integer.parseInt(assignNum),dataList,Boolean.parseBoolean(backward));
        dynamic.start();
        Map<String,Object> map = new HashMap<>();
        map.put("middles",dynamic.getMiddles());
        map.put("result",dynamic.getIntermediary());
        map.put("assignNum",assignNum);
        map.put("resourceNum",dynamic.getMachineNum());
        map.put("method",Boolean.parseBoolean(backward));
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=utf-8");
        mapper.writeValue(response.getOutputStream(),map);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(request.getContextPath()+"/index.html");
    }
}
