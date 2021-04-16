package utils;

public class ArrayWriter<AnyType> {
    /**
     * Static method, print log information.
     * @param x array
     */
    public static void printDoubleArrayToConsole(double [] x)
    {
        for (int i = 0; i < x.length; i++)
        {
            if (i % 8 != 0)
            {
                System.out.print(x[i] + "\t");
            }
            else
            {
                System.out.print(x[i] + "\n");
            }
        }
    }

    public static void print2DTabbedDoubleArrayToConsole(double [][] x)
    {
        for (int i = 0; i < x.length; i++)
        {
            for (int j = 0; j < x[0].length; j++)
            {
                if (j % 8 != 0)
                {
                    System.out.print(x[i][j] + "\t");
                }
                else
                {
                    System.out.print(x[i][j] + "\n");
                }
            }
            System.out.print("\n");
        }
    }
}
