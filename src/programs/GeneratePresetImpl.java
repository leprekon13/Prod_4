package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GeneratePresetImpl implements GeneratePreset {

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        // Создаем объект армии
        Army computerArmy = new Army();
        List<Unit> selectedUnits = new ArrayList<>();
        int currentPoints = 0;

        // Сортируем юниты по их эффективности
        unitList.sort(Comparator.comparingDouble(
                unit -> -((double) (unit.getBaseAttack() + unit.getHealth()) / unit.getCost())
        ));

        // Добавляем юниты в армию, пока не исчерпаны очки
        for (Unit unit : unitList) {
            int unitsToAdd = Math.min(11, (maxPoints - currentPoints) / unit.getCost());
            for (int i = 0; i < unitsToAdd; i++) {
                Unit newUnit = cloneUnitWithUniqueName(unit, i);
                selectedUnits.add(newUnit);
                currentPoints += unit.getCost();
            }
        }

        // Назначаем юнитам случайные координаты
        assignRandomCoordinates(selectedUnits);

        // Устанавливаем юниты и очки в армию
        computerArmy.setUnits(selectedUnits);
        computerArmy.setPoints(currentPoints);

        return computerArmy;
    }

    // Клонирует юнит с уникальным именем
    private Unit cloneUnitWithUniqueName(Unit unit, int index) {
        Unit newUnit = new Unit(unit.getName(), unit.getUnitType(), unit.getHealth(),
                unit.getBaseAttack(), unit.getCost(), unit.getAttackType(),
                unit.getAttackBonuses(), unit.getDefenceBonuses(), -1, -1);
        newUnit.setName(unit.getUnitType() + " " + index);
        return newUnit;
    }

    // Назначает случайные координаты юнитам
    private void assignRandomCoordinates(List<Unit> units) {
        Set<String> occupiedCoords = new HashSet<>();
        Random random = new Random();

        for (Unit unit : units) {
            int x, y;
            do {
                x = random.nextInt(3);  // Координаты X от 0 до 2
                y = random.nextInt(21); // Координаты Y от 0 до 20
            } while (occupiedCoords.contains(x + "," + y));
            occupiedCoords.add(x + "," + y);
            unit.setxCoordinate(x);
            unit.setyCoordinate(y);
        }
    }
}
