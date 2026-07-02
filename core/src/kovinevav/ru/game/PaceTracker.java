package kovinevav.ru.game;

import com.badlogic.gdx.utils.Array;

public class PaceTracker {
    private static final float PYATYUNYA_PACE_DELTA = -0.3f;
    private static final float PYATYUNYA_DURATION = 18f;
    private static final float PUDDLE_PACE_DELTA = 0.5f;
    private static final float PUDDLE_DURATION = 3f;

    private static final float MIN_EFFECTIVE_PACE = 4.0f;
    private static final float MAX_EFFECTIVE_PACE = 15f;

    private static class TimedModifier {
        float value;
        float timeLeft;
    }

    private final Array<TimedModifier> modifiers = new Array<>();
    private float raceClockSeconds;
    private float basePaceMinPerKm = RunRules.TARGET_PACE_MIN_PER_KM;

    public void reset() {
        raceClockSeconds = 0f;
        modifiers.clear();
    }

    public void setBasePaceMinPerKm(float paceMinPerKm) {
        basePaceMinPerKm = paceMinPerKm;
    }

    public void update(float delta, float currentSpeed) {
        float speed = Math.max(1f, currentSpeed);
        float metersPerSecond = speed * RunRules.METERS_PER_SPEED_UNIT_PER_SECOND;
        float effectivePace = clampPace(getDisplayPaceMinPerKm());
        float raceClockPerMeter = effectivePace * 60f / 1000f;
        raceClockSeconds += delta * metersPerSecond * raceClockPerMeter;
        tickModifiers(delta);
    }

    private static float clampPace(float pace) {
        return Math.max(MIN_EFFECTIVE_PACE, Math.min(MAX_EFFECTIVE_PACE, pace));
    }

    public void onPyatyunyaCollected() {
        addModifier(PYATYUNYA_PACE_DELTA, PYATYUNYA_DURATION);
    }

    public void onPuddleHit() {
        addModifier(PUDDLE_PACE_DELTA, PUDDLE_DURATION);
    }

    private void addModifier(float value, float duration) {
        TimedModifier mod = new TimedModifier();
        mod.value = value;
        mod.timeLeft = duration;
        modifiers.add(mod);
    }

    private void tickModifiers(float delta) {
        for (int i = modifiers.size - 1; i >= 0; i--) {
            TimedModifier mod = modifiers.get(i);
            mod.timeLeft -= delta;
            if (mod.timeLeft <= 0f) {
                modifiers.removeIndex(i);
            }
        }
    }

    private float activeModifierSum() {
        float sum = 0f;
        for (TimedModifier mod : modifiers) {
            sum += mod.value;
        }
        return sum;
    }

    public float getRaceClockSeconds() {
        return raceClockSeconds;
    }

    public float getBasePaceMinPerKm() {
        return basePaceMinPerKm;
    }

    public float getDisplayPaceMinPerKm() {
        return clampPace(getBasePaceMinPerKm() + activeModifierSum());
    }

    public int getActivePyatyunyaBonusCount() {
        int count = 0;
        for (TimedModifier mod : modifiers) {
            if (mod.value < 0f) {
                count++;
            }
        }
        return count;
    }

    public float getAveragePaceMinPerKm(float distanceMeters) {
        if (distanceMeters < 1f) {
            return RunRules.TARGET_PACE_MIN_PER_KM;
        }
        return (raceClockSeconds / 60f) / (distanceMeters / 1000f);
    }
}
