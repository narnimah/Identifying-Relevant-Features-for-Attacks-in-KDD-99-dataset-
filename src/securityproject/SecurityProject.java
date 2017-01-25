package securityproject;

import java.io.*;
import java.util.*;

public class SecurityProject {

    static int attrcount = 0;
    static int instcount = 0;

    public static double getEntropy(int[] x, int total) {
        double res = 0;

        for (int i = 0; i < x.length; i++) {
            if (x[i] == 0) {
                continue;
            }
            res = res - (((double) x[i] / total) * (Math.log((double) x[i] / total) / Math.log((double)2.0)));
        }
        //System.out.println(res * -1);
        return (res);
    }

    public static long[] getPercentWithClass(long[] arr, float per) {
        int attrcount = (int) (arr.length * (per / 100));
        long[] thirtyPercent = new long[attrcount + 1];
        int i;
        for (i = 0; i < attrcount; i++) {
            thirtyPercent[i] = arr[i];
        }
        thirtyPercent[i] = arr[arr.length - 1];
        System.out.println(Arrays.toString(thirtyPercent));
        return thirtyPercent;
    }

    public static boolean existsInLongArr(long[] arr, long key, int cnt) {
        int i;
        if (arr == null) {
            return false;
        }
        for (i = 0; i < cnt; i++) {
            if (arr[i] == key) {
                return true;
            }
        }
        return false;

    }

    public static int getMaxIndex(double[] arr, long[] skip, int temp) {
        double min = Double.MIN_VALUE;
        int minind = -1, i;
        for (i = 0; i < arr.length; i++) {
            if (arr[i] > min && !existsInLongArr(skip, i, temp)) {
                min = arr[i];
                minind = i;
            }
        }
        return minind;
    }

    public static void getInformationGain(String inputFile, String thisClass) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        String line;
        String[] temparr = br.readLine().split(",");
        attrcount = temparr.length;
        String[] attr;
        Hashtable<String, Integer> classesHT = new Hashtable<>();
        Hashtable<String, Integer>[] attrFreqHT = new Hashtable[attrcount-1]; //count frequency of occurrence of each attribute value
        Hashtable<String, Integer>[] attrValHT = new Hashtable[attrcount - 1]; //hold all values of an attribute

        for (int i = 0; i < attrcount - 1; i++) {
            attrValHT[i] = new Hashtable<String, Integer>();
            attrFreqHT[i]=new Hashtable<String, Integer>();
        }
        while ((line = br.readLine()) != null) {
            attr = line.split(",");
            instcount++;
            for (int i = 0; i < attr.length; i++) {

                if (i == attr.length - 1) {
                    if(!attr[i].equals(thisClass)){
                        attr[i]="others";
                    }
                    if (classesHT.containsKey(attr[i])) {
                        classesHT.put(attr[i], classesHT.get(attr[i]) + 1);
                    } else {
                        classesHT.put(attr[i], 1);
                    }
                } else {
                    if (attrFreqHT[i].containsKey(attr[i] + "," + attr[attr.length - 1])) {
                        attrFreqHT[i].put(attr[i] + "," + attr[attr.length - 1], attrFreqHT[i].get(attr[i] + "," + attr[attr.length - 1]) + 1);
                    } else {
                        attrFreqHT[i].put(attr[i] + "," + attr[attr.length - 1], 1);
                    }
                    if (attrFreqHT[i].containsKey(attr[i] + ",total")) {
                        attrFreqHT[i].put(attr[i] + ",total", attrFreqHT[i].get(attr[i] + ",total") + 1);
                    } else {
                        attrFreqHT[i].put(attr[i] + ",total", 1);
                    }
                    if (!attrValHT[i].containsKey(attr[i])) {
                        attrValHT[i].put(attr[i], 1);

                    }

                }
            }
        }

        double withclass = 0;
        double[] gain = new double[attrcount - 1];
        Hashtable<Double, Integer> gainHT = new Hashtable<>();
        for (int i = 0; i < attrcount - 1; i++) {
            Set<String> vals = attrValHT[i].keySet();
            double prob = 0;
            withclass = 0;
            for (String s : vals) {
                prob = ((float) attrFreqHT[i].get(s + ",total") / instcount);
                System.out.println(s);
                int[] temp = new int[classesHT.size()];
                int j = 0;
                for (String c : classesHT.keySet()) {
                    if (attrFreqHT[i].containsKey(s + "," + c)) {
                        temp[j++] = attrFreqHT[i].get(s + "," + c);
                    } else {
                        temp[j++] = 0;
                    }
                }
                withclass = withclass + (prob * getEntropy(temp, attrFreqHT[i].get(s + ",total")));
                System.out.println((getEntropy(temp, attrFreqHT[i].get(s + ",total"))));

            }

            int[] classfreq = new int[classesHT.size()];
            int j = 0;
            for (String c : classesHT.keySet()) {
                classfreq[j++] = classesHT.get(c);
            }
            gain[i] = getEntropy(classfreq, instcount) - withclass;

        }
        //System.out.println(Arrays.toString(gain));
        //Arrays.sort(gain);
        long[] attrs = new long[attrcount];
        long[] skip = new long[attrcount];
        int x = 0;
        skip[0] = attrcount - 1;
        for (int i = 0; i < attrcount - 1; i++) {
            attrs[i] = getMaxIndex(gain, skip, i + 1);
            skip[i + 1] = attrs[i];
        }
        attrs[attrcount - 1] = attrcount - 1;
        getPercentWithClass(attrs, 30);

    }

    public static void main(String[] args) throws IOException {
        getInformationGain("preprocessed.csv","dos.");

    }

}
