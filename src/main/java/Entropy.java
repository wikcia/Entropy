import java.io.*;

class Entropy {

    public static void main(String[] args) {

        String filepath = args[0];

        File file = new File(filepath);
        try (FileInputStream fin = new FileInputStream(file)){
            byte fileContent[] = new byte[(int) file.length()];

            // Read data into the byte array
            // int read(byte[] b) - this method is used to read up to b.length bytes of data from the input stream.
            fin.read(fileContent);

            // create an array to keep track of frequency of bytes
            int [] characterOccurrencesInAFile = new int[256];

            // create an array to keep track frequency of occurrences after another character
            int [][] characterOccurrencesAfterAnother = new int[256][256];

            int fileContentLength = fileContent.length - 1;


            int previous = 0;
            // count frequency of occurring bytes
            for(int i = 0; i < fileContentLength; i++) {

                byte byteValue = fileContent[i];

                int castedValue = Byte.toUnsignedInt(byteValue);

                characterOccurrencesInAFile[castedValue]++;
                characterOccurrencesAfterAnother[previous][castedValue]++;

                previous = castedValue;
            }

            // calculate entropy
            double entropy = getEntropy(characterOccurrencesInAFile, fileContentLength);
            // calculate conditional entropy
            double conditionalEntropy = ConditionalEntropy(characterOccurrencesInAFile,characterOccurrencesAfterAnother,fileContentLength);

            // output the entropy calculated
            System.out.println("Entropy: " + entropy);
            System.out.println("Conditional entropy: " + conditionalEntropy);
            System.out.println("Difference: " + (entropy - conditionalEntropy));
        }

        catch (FileNotFoundException e) {
            System.out.println("File not found" + e);
        }

        catch (IOException ioe) {
            System.out.println("Exception while reading file " + ioe);
        }
    }

    private static double ConditionalEntropy(int[] characterOccurrencesInAFile,  int[][] characterOccurrencesAfterAnother, double fileContentLength)
    {
        double output = 0.0;
        int possibleCharactersCount = 256;
        for (int x = 0; x < possibleCharactersCount; x++)
        {
            for (int y = 0; y < possibleCharactersCount; y++)
            {
                if (characterOccurrencesInAFile[x] != 0  && characterOccurrencesAfterAnother[x][y] != 0)
                output += 1.0 * characterOccurrencesAfterAnother[x][y] / fileContentLength * (-1 * customLog(2, characterOccurrencesAfterAnother[x][y]) + customLog(2,characterOccurrencesInAFile[x]));
            }
        }
        return output;
    }

    // alternative way to calculate entropy

    private static double getEntropy2(int[] characterOccurrencesInAFile, double fileContentLength) {
        double output = 0.0;
        int possibleCharactersCount = 256;
        int count = 0;
        for (int x = 0; x < possibleCharactersCount; x++) {
            if (characterOccurrencesInAFile[x] != 0)
            {
                output += -1.0 *  customLog(2,characterOccurrencesInAFile[x]) * characterOccurrencesInAFile[x];
                count++;
            }
        }
        output = output / fileContentLength;
        return output + customLog(2,fileContentLength);
    }

    private static double getEntropy(int[] characterOccurrencesInAFile, double fileContentLength) {
        double entropy = 0;
        for(int i = 0; i < characterOccurrencesInAFile.length; i++) {
            if(characterOccurrencesInAFile[i] != 0) {
                // calculate the probability of a particular byte occurring
                double probabilityOfByte = (double) characterOccurrencesInAFile[i] / fileContentLength;

                // calculate the next value to sum to previous entropy calculation
                double value = probabilityOfByte * customLog(2,probabilityOfByte);

                entropy = entropy + value;
            }
        }
        entropy = entropy * -1;
        return entropy;
    }

    // function to evaluate the value of a logarithm with a custom base
    private static double customLog(double base, double logNumber) {
        return Math.log(logNumber) / Math.log(base);
    }
}