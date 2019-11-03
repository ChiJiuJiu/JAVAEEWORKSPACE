package mx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import mx.dijkstra.Dijkstra;
import mx.dijkstra.Edge;
import mx.dijkstra.Point;

public class RunDome {

    public int[][] initByEdges(ArrayList<Edge> edges, int n) {
        int[][] weights = new int[n + 1][n + 1];
        Iterator<Edge> iterator = edges.iterator();
        while (iterator.hasNext()) {
            Edge edge = iterator.next();
            Point start = edge.getStart();
            Point end = edge.getEnd();
            weights[((MyPoint)start).getId()][((MyPoint)end).getId()] = edge.getWeight();
        }
        return weights;
    }

    public void demo1() {
        // 声明点
        Point A = new MyPoint(1, 0, 0);
        Point B = new MyPoint(2, 0, 0);
        Point C = new MyPoint(3, 0, 0);
        Point D = new MyPoint(4, 0, 0);
        Point E = new MyPoint(5, 0, 0);
        // 放入点集合
        ArrayList<Point> source = new ArrayList<Point>();
        source.add(A);
        source.add(B);
        source.add(C);
        source.add(D);
        source.add(E);
        // 声明边
        ArrayList<Edge> edges = new ArrayList<Edge>();
        edges.add(new Edge(A, B, 10));
        edges.add(new Edge(A, C, 5));
        edges.add(new Edge(B, C, 2));
        edges.add(new Edge(B, D, 1));
        edges.add(new Edge(C, B, 3));
        edges.add(new Edge(C, D, 9));
        edges.add(new Edge(C, E, 2));
        edges.add(new Edge(D, E, 4));
        edges.add(new Edge(E, D, 6));
        edges.add(new Edge(E, A, 7));

        int[][] weights = initByEdges(edges, 5);

        List<Point> ends = new LinkedList<>();
        ends.add(B);
        ends.add(C);
        ends.add(D);
        ends.add(E);
        int i = 0, count = 0;
        int length = 0;
        Point pre = null, next = null;
        while (i++ < ends.size()) {
            Point end = ends.get(i - 1);
            Dijkstra d = new Dijkstra();
            Stack<Point> points = d.dijkstra(source, edges, A, end);// 提供 点的集合 边的集合 起点 终点 开始寻径
            while (points.size() > 0) {
                Point p = points.pop();
                if (count++ == 0) {
                    pre = p;
                } else {
                    next = p;
                }
                // 计算路径长度
                if (pre != null && next != null)
                    length += weights[((MyPoint)pre).getId()][((MyPoint)next).getId()];
                if (next != null)
                    pre = next;
                System.out.print(((MyPoint)p).getId());// 打印
                if (points.size() > 0)
                    System.out.print("->");
            }
            count = 0;
            System.out.println("最短路径长度：" + length);
            length = 0;
            pre = next = null;
        }

    }

    public static void main(String[] args) {
        new RunDome().demo1();
    }
}
