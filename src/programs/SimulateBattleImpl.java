package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SimulateBattleImpl implements SimulateBattle {
    private PrintBattleLog printBattleLog;

    // Конструктор по умолчанию
    public SimulateBattleImpl() {
        // Пытаемся получить реализацию PrintBattleLog
        this.printBattleLog = resolvePrintBattleLog();
    }

    // Конструктор с передачей PrintBattleLog
    public SimulateBattleImpl(PrintBattleLog printBattleLog) {
        if (printBattleLog == null) {
            throw new IllegalArgumentException("PrintBattleLog cannot be null");
        }
        this.printBattleLog = printBattleLog;
    }

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        Set<Unit> playerUnits = new HashSet<>(playerArmy.getUnits());
        Set<Unit> computerUnits = new HashSet<>(computerArmy.getUnits());

        while (!playerUnits.isEmpty() && !computerUnits.isEmpty()) {
            executeAttacks(playerUnits, computerUnits);
            executeAttacks(computerUnits, playerUnits);
        }

        String winner = playerUnits.isEmpty() ? "Компьютер" : "Игрок";
        System.out.println("Победитель: " + winner);
    }

    private void executeAttacks(Set<Unit> attackingUnits, Set<Unit> defendingUnits) throws InterruptedException {
        Iterator<Unit> iterator = attackingUnits.iterator();

        while (iterator.hasNext()) {
            Unit attackingUnit = iterator.next();

            if (!attackingUnit.isAlive()) {
                iterator.remove();
                continue;
            }

            Unit target = attackingUnit.getProgram().attack();

            if (target != null) {
                printBattleLog.printBattleLog(attackingUnit, target);

                if (!target.isAlive()) {
                    defendingUnits.remove(target);
                }
            }

            Thread.sleep(500); // Можно регулировать
        }
    }

    // Заглушка для PrintBattleLog
    private PrintBattleLog resolvePrintBattleLog() {
        return new PrintBattleLog() {
            @Override
            public void printBattleLog(Unit attackingUnit, Unit target) {
                System.out.printf("Атакующий: %s -> Цель: %s%n", attackingUnit.getName(), target.getName());
            }
        };
    }
}
