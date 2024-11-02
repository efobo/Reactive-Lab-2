import entities.Manufacturer;
import entities.Product;
import generators.ManufacturerGenerator;
import generators.ProductGenerator;
import statistics.Calculation;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        int manufacturerCount = 3;
        int reviewCount = 100;
        int[] productCounts = {5000, 50000, 250000};
        boolean printCalculationResults = false;

        for (int i = 1; i <= productCounts.length; i++) {
            System.out.printf("--------------Test %d--------------\n\n", i);
            System.out.printf("Generating %d manufacturers... ", manufacturerCount);

            ManufacturerGenerator manufacturerGenerator = new ManufacturerGenerator();
            List<Manufacturer> manufacturers = manufacturerGenerator.generateList(manufacturerCount);

            System.out.println("Done.");
            System.out.printf("Generating %d products with %d reviews... ", productCounts[i - 1], reviewCount);

            ProductGenerator productGenerator = new ProductGenerator(manufacturers, reviewCount);
            List<Product> products = productGenerator.generateList(productCounts[i - 1]);

            System.out.println("Done.\n");
            System.out.print("Calculating with iterative loop... ");

            long startTime = System.currentTimeMillis();
            Map<Manufacturer, Double> avgRatingWithLoop = Calculation.avgRatingWithLoop(products, manufacturers);
            long timeWithLoop = System.currentTimeMillis() - startTime;

            System.out.println("Done.");
            if (printCalculationResults) {
                System.out.println("Calculation results:");
                Calculation.printResult(avgRatingWithLoop);
                System.out.println();
            }

            System.out.print("Calculating with pipeline of standard collectors... ");

            startTime = System.currentTimeMillis();
            Map<Manufacturer, Double> avgRatingWithPipeline = Calculation.avgRatingWithPipeline(products, manufacturers);
            long timeWithPipeline = System.currentTimeMillis() - startTime;

            System.out.println("Done.");
            if (printCalculationResults) {
                System.out.println("Calculation results:");
                Calculation.printResult(avgRatingWithPipeline);
                System.out.println();
            }

            System.out.print("Calculating with custom collector... ");

            startTime = System.currentTimeMillis();
            Map<Manufacturer, Double> avgRatingWithCollector = Calculation.avgRatingWithCollector(products, manufacturers);
            long timeWithCollector = System.currentTimeMillis() - startTime;

            System.out.println("Done.\n");
            if (printCalculationResults) {
                System.out.println("Calculation results:");
                Calculation.printResult(avgRatingWithCollector);
                System.out.println();
            }

            System.out.println("Calculation times:");
            System.out.printf(tableFormat, timeWithLoop, timeWithPipeline, timeWithCollector);
        }
    }

    private static final String tableFormat = """
            ┌───────────┬───────────┬───────────┐
            │      loop │  pipeline │ collector │
            ├───────────┼───────────┼───────────┤
            │ %6d ms │ %6d ms │ %6d ms │
            └───────────┴───────────┴───────────┘
            
            """;
}