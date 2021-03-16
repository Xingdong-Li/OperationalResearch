package Servlets;

import Model.Fraction;
import SimplexMethod.SimplexMethodSolver;
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
 * @since 2020.11.6
 */
@WebServlet("/simplexMethodServlet")
public class SimplexMethodServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String values = request.getParameter("values");
        String checkNums = request.getParameter("checkNums");
        String bs = request.getParameter("bs");
        String variableNum = request.getParameter("variableNum");
        if (values == null || checkNums == null || bs == null || variableNum == null) {
            response.sendRedirect(request.getContextPath() + "/index.html");
            return;
        }
        String rawValues = values.substring(1, values.length() - 1);
        List<Integer> rawList = new ArrayList<>();//参数
        for (String str : rawValues.split(",")) {
            rawList.add(Integer.parseInt(str));
        }
        String rawBs = bs.substring(1, bs.length() - 1);
        List<Integer> bsList = new ArrayList<>();
        for (String str : rawBs.split(",")) {
            bsList.add(Integer.parseInt(str));
        }
        List<Fraction> dataList = new ArrayList<>();
        int varNum = Integer.parseInt(variableNum);
        int constraintNum = rawList.size() / varNum;
        for (int i = 0; i < constraintNum; i++) {
            dataList.add(new Fraction(bsList.get(i)));
            for (int j = 0; j < varNum; j++) {
                dataList.add(new Fraction(rawList.get(varNum * i + j)));
            }
        }
        String rawCheckNum = checkNums.substring(1, checkNums.length() - 1);
        List<Fraction> Cb = new ArrayList<>();
        for (String str : rawCheckNum.split(",")) {
            Cb.add(new Fraction(Integer.parseInt(str)));
        }
        SimplexMethodSolver solver = new SimplexMethodSolver(varNum, dataList, Cb);
        solver.start();
        Map<String, Object> result = new HashMap<>();
        result.put("indexes", solver.getIndexes());
        result.put("checks", solver.getCheckNum());
        result.put("tempChecks", solver.getTempChecks());
        result.put("objs", solver.getObjs());
        result.put("matrices", solver.getMatrixList());
        result.put("xbs", solver.getXbs());
        result.put("resultType", solver.getResultType());
        result.put("constraintNum", constraintNum);
        result.put("thetas", solver.getThetas());
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=utf-8");
        mapper.writeValue(response.getOutputStream(), result);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/index.html");
    }
}
