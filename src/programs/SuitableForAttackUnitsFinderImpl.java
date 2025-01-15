package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();

        // Идем по строкам и ищем подходящие юниты
        for (List<Unit> row : unitsByRow) {
            suitableUnits.addAll(findSuitableUnitsInRow(row, isLeftArmyTarget));
        }

        return suitableUnits;
    }

    // Поиск подходящих юнитов в строке
    private List<Unit> findSuitableUnitsInRow(List<Unit> row, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();

        for (int index = 0; index < row.size(); index++) {
            Unit unit = row.get(index);

            if (unit != null && unit.isAlive()) {
                boolean isSuitable = isLeftArmyTarget
                        ? isRightmostUnit(row, index)
                        : isLeftmostUnit(row, index);

                if (isSuitable) {
                    suitableUnits.add(unit);
                }
            }
        }

        return suitableUnits;
    }

    // Проверка: является ли юнит самым правым
    private boolean isRightmostUnit(List<Unit> row, int unitIndex) {
        for (int i = unitIndex + 1; i < row.size(); i++) {
            if (row.get(i) != null && row.get(i).isAlive()) {
                return false;
            }
        }
        return true;
    }

    // Проверка: является ли юнит самым левым
    private boolean isLeftmostUnit(List<Unit> row, int unitIndex) {
        for (int i = 0; i < unitIndex; i++) {
            if (row.get(i) != null && row.get(i).isAlive()) {
                return false;
            }
        }
        return true;
    }
}
