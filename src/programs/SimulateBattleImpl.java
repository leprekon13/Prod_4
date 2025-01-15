package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SimulateBattleImpl implements SimulateBattle {
    private final PrintBattleLog printBattleLog;

    public SimulateBattleImpl(PrintBattleLog printBattleLog) {
        this.printBattleLog = printBattleLog;
    }

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        // Храним юнитов обеих армий в наборах для быстрого удаления
        Set<Unit> playerUnits = new HashSet<>(playerArmy.getUnits());
        Set<Unit> computerUnits = new HashSet<>(computerArmy.getUnits());

        // Пока обе армии имеют живых юнитов
        while (!playerUnits.isEmpty() && !computerUnits.isEmpty()) {
            // Атака игрока по компьютеру
            executeAttacks(playerUnits, computerUnits);

            // Атака компьютера по игроку
            executeAttacks(computerUnits, playerUnits);
        }

        // Определяем победителя
        String winner = playerUnits.isEmpty() ? "Компьютер" : "Игрок";
        System.out.println("Победитель: " + winner);
    }

    private void executeAttacks(Set<Unit> attackingUnits, Set<Unit> defendingUnits) throws InterruptedException {
        Iterator<Unit> iterator = attackingUnits.iterator();

        while (iterator.hasNext()) {
            Unit attackingUnit = iterator.next();

            // Убираем мертвых юнитов
            if (!attackingUnit.isAlive()) {
                iterator.remove();
                continue;
            }

            // Атакуем цель
            Unit target = attackingUnit.getProgram().attack();

            if (target != null) {
                printBattleLog.printBattleLog(attackingUnit, target);

                // Убираем мертвых юнитов из защищающейся армии
                if (!target.isAlive()) {
                    defendingUnits.remove(target);
                }
            }

            // Задержка для визуализации боя
            Thread.sleep(500); // Можно регулировать
        }
    }
}
