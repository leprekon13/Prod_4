package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.EdgeDistance;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {

    private static final int WIDTH = 27; // Ширина игрового поля
    private static final int HEIGHT = 21; // Высота игрового поля
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        // Массив расстояний
        int[][] distance = initializeDistanceArray();
        boolean[][] visited = new boolean[WIDTH][HEIGHT];
        Edge[][] previous = new Edge[WIDTH][HEIGHT];
        Set<String> occupiedCells = getOccupiedCells(existingUnitList, attackUnit, targetUnit);

        // Очередь для обработки клеток
        PriorityQueue<EdgeDistance> queue = new PriorityQueue<>(Comparator.comparingInt(EdgeDistance::getDistance));
        initializeStartPoint(attackUnit, distance, queue);

        // Выполняем алгоритм поиска пути
        while (!queue.isEmpty()) {
            EdgeDistance current = queue.poll();
            if (visited[current.getX()][current.getY()]) continue;
            visited[current.getX()][current.getY()] = true;

            // Проверяем, достигли ли цели
            if (isTargetReached(current, targetUnit)) {
                break;
            }

            // Обрабатываем соседей текущей клетки
            exploreNeighbors(current, occupiedCells, distance, previous, queue);
        }

        // Строим путь
        return constructPath(previous, attackUnit, targetUnit);
    }

    // Инициализация массива расстояний
    private int[][] initializeDistanceArray() {
        int[][] distance = new int[WIDTH][HEIGHT];
        for (int[] row : distance) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        return distance;
    }

    // Генерация карты занятых клеток
    private Set<String> getOccupiedCells(List<Unit> existingUnitList, Unit attackUnit, Unit targetUnit) {
        Set<String> occupiedCells = new HashSet<>();
        for (Unit unit : existingUnitList) {
            if (unit.isAlive() && unit != attackUnit && unit != targetUnit) {
                occupiedCells.add(unit.getxCoordinate() + "," + unit.getyCoordinate());
            }
        }
        return occupiedCells;
    }

    // Инициализация стартовой точки
    private void initializeStartPoint(Unit attackUnit, int[][] distance, PriorityQueue<EdgeDistance> queue) {
        int startX = attackUnit.getxCoordinate();
        int startY = attackUnit.getyCoordinate();
        distance[startX][startY] = 0;
        queue.add(new EdgeDistance(startX, startY, 0));
    }

    // Проверка, достигли ли цели
    private boolean isTargetReached(EdgeDistance current, Unit targetUnit) {
        return current.getX() == targetUnit.getxCoordinate() && current.getY() == targetUnit.getyCoordinate();
    }

    // Обработка соседей
    private void exploreNeighbors(EdgeDistance current, Set<String> occupiedCells, int[][] distance, Edge[][] previous, PriorityQueue<EdgeDistance> queue) {
        for (int[] dir : DIRECTIONS) {
            int nx = current.getX() + dir[0];
            int ny = current.getY() + dir[1];

            if (isValid(nx, ny, occupiedCells)) {
                int newDistance = distance[current.getX()][current.getY()] + 1;

                if (newDistance < distance[nx][ny]) {
                    distance[nx][ny] = newDistance;
                    previous[nx][ny] = new Edge(current.getX(), current.getY());
                    queue.add(new EdgeDistance(nx, ny, newDistance));
                }
            }
        }
    }

    // Проверка валидности координат
    private boolean isValid(int x, int y, Set<String> occupiedCells) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT && !occupiedCells.contains(x + "," + y);
    }

    // Восстановление пути
    private List<Edge> constructPath(Edge[][] previous, Unit attackUnit, Unit targetUnit) {
        List<Edge> path = new ArrayList<>();
        int pathX = targetUnit.getxCoordinate();
        int pathY = targetUnit.getyCoordinate();

        while (pathX != attackUnit.getxCoordinate() || pathY != attackUnit.getyCoordinate()) {
            path.add(new Edge(pathX, pathY));
            Edge prev = previous[pathX][pathY];
            if (prev == null) return Collections.emptyList(); // Путь не найден
            pathX = prev.getX();
            pathY = prev.getY();
        }

        path.add(new Edge(attackUnit.getxCoordinate(), attackUnit.getyCoordinate()));
        Collections.reverse(path);
        return path;
    }
}
