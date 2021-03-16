package Test;

import DynamicProgramming.Dynamic;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestDynamic {
    @Test
    public void testDynamic(){
        List<Integer> data = new ArrayList<>();
        //甲乙丙
        data.add(0);data.add(0);data.add(0);
        data.add(3);data.add(5);data.add(4);
        data.add(7);data.add(10);data.add(6);
        data.add(9);data.add(11);data.add(11);
        data.add(12);data.add(11);data.add(12);
        data.add(13);data.add(11);data.add(12);
        Dynamic dynamic = new Dynamic(3,data,false);
//        dynamic.showData();
        dynamic.start();
        dynamic.showInt();
        for (Object middle : dynamic.getMiddles()) {
            List<int[]> list = (List<int[]>) middle;
            list.forEach(e->System.out.print(Arrays.toString(e)+","));
            System.out.println("---------你好");
        }
    }

    @Test
    public void testDynamic2(){
        List<Integer> data = new ArrayList<>();
        data.add(0);data.add(0);data.add(0);data.add(0);
        data.add(4);data.add(2);data.add(3);data.add(4);
        data.add(6);data.add(4);data.add(5);data.add(5);
        data.add(7);data.add(6);data.add(7);data.add(6);
        data.add(7);data.add(8);data.add(8);data.add(6);
        data.add(7);data.add(9);data.add(8);data.add(6);
        data.add(7);data.add(10);data.add(8);data.add(6);
        Dynamic dynamic = new Dynamic(4,data);
        dynamic.start();
        dynamic.showInt();
    }

    @Test
    public void testDynamic3() {
        List<Integer> data = new ArrayList<>();
        data.add(0);data.add(0);data.add(0);
        data.add(41);data.add(42);data.add(64);
        data.add(48);data.add(50);data.add(68);
        data.add(60);data.add(60);data.add(78);
        data.add(66);data.add(66);data.add(76);
        Dynamic dynamic = new Dynamic(3,data);
        dynamic.start();
        dynamic.showInt();
    }

    @Test
    public void testDynamic4() {
        List<Integer> data = new ArrayList<>();
        data.add(1);data.add(2);
        Dynamic dynamic = new Dynamic(1,data);
        dynamic.start();
        dynamic.showInt();
    }
}
