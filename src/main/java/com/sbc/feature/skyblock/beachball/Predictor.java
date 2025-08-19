package com.sbc.feature.skyblock.beachball;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.math4.legacy.fitting.PolynomialCurveFitter;
import org.apache.commons.math4.legacy.fitting.WeightedObservedPoint;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Predicts the trajectory of a moving object (like a beach ball) by fitting its
 * observed positions to a quadratic curve.
 * <p>
 * This class collects position data over time, then uses polynomial regression
 * to model the path for the X, Y, and Z axes. It assumes a standard projectile
 * motion model where acceleration is constant, resulting in a quadratic equation for position over time.
 */
public class Predictor {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private boolean goingUp = true;
    private long startTime;
    private Vec3d last;
    private final HashMap<Long, Vec3d> data = new HashMap<>();
    public final double originalDistance;

    public double[][] coeffs;
    private final ArrayList<Vec3d> predictedPath = new ArrayList<>();

    public Predictor(long time, Vec3d pos){
        this.startTime = time;
        this.last = pos;
        this.originalDistance = pos.distanceTo(client.player.getPos());
    }

    public void update(long time, Vec3d pos){
        if (pos.squaredDistanceTo(last) < 0.01) return;
        if (!goingUp && pos.y - last.y > 0){
            data.clear();
            startTime = time;
        }

        data.put(time - startTime, pos);
        goingUp = pos.y - last.y > 0;
        last = pos;

        if (data.size() % 3 == 0){
            predict();
        }
    }

    /**
     * Analyzes the collected position data to predict the future trajectory.
     * <p>
     * This method tests several sets of data points (e.g., the last three, or the first, middle, and last)
     * to find the best-fitting quadratic model. The model is then used to extrapolate the future path
     * of the object until it hits a block or goes below the world.
     */
    private void predict() {
        ArrayList<Long> ticks = new ArrayList<>(data.keySet());
        int n = ticks.size();

        if (n < 3) return;
    
        ArrayList<int[]> models = new ArrayList<>();

        // Model A: last 3 consecutive
        if (n >= 3) models.add(new int[]{n - 3, n - 2, n - 1});

        // Model B: last 1st, 3rd, 5th
        if (n >= 5) models.add(new int[]{n - 5, n - 3, n - 1});

        // Model C: first, middle, last
        if (n >= 3) models.add(new int[]{0, n / 2, n - 1});

        ArrayList<double[]> xModels = new ArrayList<>();
        ArrayList<double[]> yModels = new ArrayList<>();
        ArrayList<double[]> zModels = new ArrayList<>();

        for (int[] idxs : models) {
            ArrayList<WeightedObservedPoint> xPoints = new ArrayList<>();
            ArrayList<WeightedObservedPoint> yPoints = new ArrayList<>();
            ArrayList<WeightedObservedPoint> zPoints = new ArrayList<>();

            for (int i : idxs) {
                long t = ticks.get(i);
                Vec3d v = data.get(t);
                xPoints.add(new WeightedObservedPoint(1.0, t, v.x));
                yPoints.add(new WeightedObservedPoint(1.0, t, v.y));
                zPoints.add(new WeightedObservedPoint(1.0, t, v.z));
            }

            // Fit all axes to a quadratic model (degree 2) to accurately model projectile motion.
            PolynomialCurveFitter quadFitter = PolynomialCurveFitter.create(2);
            double[] xQuad = quadFitter.fit(xPoints);
            double[] yQuad = quadFitter.fit(yPoints);
            double[] zQuad = quadFitter.fit(zPoints);
            xModels.add(xQuad);
            yModels.add(yQuad);
            zModels.add(zQuad);
        }

        int bestIdx = 0;
        double minError = Double.MAX_VALUE;

        for (int i = 0; i < yModels.size(); i++) {
            double err = 0;
            for (int j = 0; j < ticks.size(); j++) {
                long t = ticks.get(j);
                Vec3d v = data.get(t);
                double yPred = yModels.get(i)[0] + yModels.get(i)[1] * t + yModels.get(i)[2] * t * t;
                err += Math.pow(v.y - yPred, 2);
            }
            if (err < minError) {
                minError = err;
                bestIdx = i;
            }
        }

        coeffs = new double[3][];
        coeffs[0] = xModels.get(bestIdx);  // X quad: [a, b, c]
        coeffs[1] = yModels.get(bestIdx);  // Y quad: [a, b, c]
        coeffs[2] = zModels.get(bestIdx);  // Z quad: [a, b, c]

        // === Predict path ===
        predictedPath.clear();

        ticks.sort(Long::compareTo);
        for (long t : ticks) {
            Vec3d v = data.get(t);
            predictedPath.add(v);
        }

        // Then append 300 predicted future points
        long lastTick = ticks.get(ticks.size() - 1);

        for (int i = 1; i <= 300; i++) {
            long t = lastTick + i;
            double x = coeffs[0][0] + coeffs[0][1] * t + coeffs[0][2] * t * t;
            double y = coeffs[1][0] + coeffs[1][1] * t + coeffs[1][2] * t * t;
            double z = coeffs[2][0] + coeffs[2][1] * t + coeffs[2][2] * t * t;
            predictedPath.add(new Vec3d(x, y, z));
            if (!client.world.getBlockState(BlockPos.ofFloored(x, y, z)).isAir() || y < -64) break;
        }
    }

    public ArrayList<Vec3d> getPath(){
        return predictedPath;
    }

    public Vec3d getLandingPos(){
        return predictedPath.get(predictedPath.size() - 1);
    }

    @Nullable
    public Vec3d getLandingPos(double yLevel) {
        for (int i = predictedPath.size() - 2; i >= 0; i--) {
            Vec3d curr = predictedPath.get(i);
            Vec3d next = predictedPath.get(i + 1);

            if ((curr.y - yLevel) * (next.y - yLevel) <= 0) {
                double t = (yLevel - curr.y) / (next.y - curr.y);
                double x = curr.x + t * (next.x - curr.x);
                double y = yLevel;
                double z = curr.z + t * (next.z - curr.z);
                return new Vec3d(x, y, z);
            }
        }
        return null;
    }

}
