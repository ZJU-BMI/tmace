package cn.edu.zju.bme.model;

import cn.edu.zju.bme.data.Document;
import cn.edu.zju.bme.data.Documents;
import cn.edu.zju.bme.data.Sentence;
import cn.edu.zju.bme.util.Util;
import lombok.Getter;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static cn.edu.zju.bme.util.Util.*;

@Getter
public class TMACE implements Serializable{
    private double alpha;
    private double beta;
    private double gamma;
    private double lambda;

    private int D; // number of documents
    private int V; // number of words

    private int Z; // number of MACE types
    private int R; // number of severity degrees

    private int iterations;

    private double[][][] phi; // Z * R * W
    private double[] phiB; // W
    private double[][] pi; // Z * R
    private double[] theta; // Z

    private int[][] dsz; // z of sth sentence of dth document
    private int[][] dsr; // r of sth sentence of dth document

    private int counts;
    private int[] zs; // z
    private int[][] zr; // Z * R
    private int[][][] zrw; // Z * R * W
    private int zb; // number of word assigned to background topic
    private int[] zbw; // W


    private Map<String, Integer> bagOfWord;
    private List<String> indexToWord;

    public TMACE() {
        alpha = 1;
        beta = 0.1;
        gamma = 0.1;
        lambda = 0.2;
        Z = 4;
        R = 2;
        iterations = 1000;
    }

    public TMACE(double alpha, double beta, double gamma, double lambda, int Z, int R, int iterations) {
        assert lambda > 0 && lambda < 1;

        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
        this.lambda = lambda;
        this.Z = Z;
        this.R = R;
        this.iterations = iterations;
    }

    public void init(Documents documents, List<String> indexToWord, Map<String, Integer> bagOfWord) {
        this.bagOfWord = bagOfWord;
        this.indexToWord = indexToWord;

        D = documents.size();
        V = indexToWord.size();

        zs = new int[Z];
        zr = new int[Z][R];
        zrw = new int[Z][R][V];
        zb = 0;
        zbw = new int[V];

        dsz = new int[D][];
        dsr = new int[D][];

        phi = new double[Z][R][V];
        phiB = new double[V];
        pi = new double[Z][R];
        theta = new double[Z];


        for (int i=0; i<D; i++) {
            Document document = documents.get(i);
            int n_sentence = document.size();
            dsz[i] = new int[n_sentence];
            dsr[i] = new int[n_sentence];

            for (int j=0; j<n_sentence; j++) {
                Sentence sentence = document.get(j);

                double y = Math.random();

                if (y < lambda) {
                    // MACE topic
                    int z = (int) (Math.random() * Z);
                    int r = (int) (Math.random() * R);

                    dsz[i][j] = z;
                    dsr[i][j] = r;

                    for (int word : sentence) {
                        zs[z]++;
                        zr[z][r]++;
                        zrw[z][r][word]++;
                    }
                } else {
                    // background topic
                    for (int word : sentence) {
                        zb++;
                        zbw[word]++;
                        dsz[i][j] = Z;
                    }
                }

                counts++;

            }
        }
    }

    public void inference(Documents documents) {

        for (int k=0; k<iterations; k++) {
            for (int d=0; d<D; d++) {
                Document document = documents.get(d);
                int n_sentences = document.size();

                for (int s=0; s<n_sentences; s++) {
                    sampleTopic(d, s, document.get(s));
                }

            }
        }
    }

    private void sampleTopic(int d, int s, Sentence sentence) {
        int old_z = dsz[d][s];
        if (old_z == Z) {
            for (int word : sentence) {
                zb--;
                zbw[word]--;
            }
        } else {
            int old_r = dsr[d][s];
            for (int word : sentence) {
                zr[old_z][old_r]--;
                zrw[old_z][old_r][word]--;
            }
        }

        // sample background topic or MACE topic
        double p = Math.random();
        if (p < lambda) {
            double[][] zr_prob = new double[Z][R];
            double[] prob = new double[R*Z];

            for (int i=0; i<Z; i++) {
                for (int j=0; j<R; j++) {
                    zr_prob[i][j] = Math.log((zs[i] + alpha) / (counts + Z * alpha))
                            + Math.log((zr[i][j] + beta) / (zs[i] + R * beta));
                    for (int word : sentence) {
                        zr_prob[i][j] = zr_prob[i][j] + Math.log((zrw[i][j][word] + gamma) / (zr[i][j] + V * gamma));
                    }

//                    zr_prob[i][j] *= lambda;
                    if (i==0 && j==0) {
                        prob[i*R+j] = -zr_prob[i][j];
                    } else {
                        prob[i*R+j] = prob[i*R+j-1] - zr_prob[i][j];
                    }
                }
            }

            double sample = Math.random() * prob[R*Z-1];
            int k;
            for (k=0; k<R*Z; k++) {
                if (sample < prob[k]) {
                    break;
                }
            }

            int new_z;
            int new_r;

            if (k == 8) {
                System.out.println("....");
            }

            new_z = k / R;
            new_r = k % R;

            dsz[d][s] = new_z;
            dsr[d][s] = new_r;
            for (int word : sentence) {
                zr[new_z][new_r]++;
                zrw[new_z][new_r][word]++;
            }

        } else {
            dsz[d][s] = Z;
            for (int word : sentence) {
                zb++;
                zbw[word]++;
            }
        }

    }

    public void updateParameter() {
        for (int i=0; i<Z; i++) {
            theta[i] = (zs[i] + alpha) / (counts + Z * alpha);
        }

        for (int i=0; i<Z; i++) {
            for (int j=0; j<R; j++) {
                pi[i][j] = (zr[i][j] + beta) / (zs[i] + R * beta);
            }
        }

        for (int w=0; w<V; w++) {
            phiB[w] = (zbw[w] + gamma)/ (zb + V * gamma);
        }

        for (int i=0; i<Z; i++) {
            for (int j=0; j<R; j++) {
                for (int w=0; w<V; w++) {
                    phi[i][j][w] = (zrw[i][j][w] + gamma) / (zr[i][j] + V * gamma);
                }
            }
        }
    }

    public void saveModel(String path) {
        writeObject(path, this);
    }

    public void inferenceResult(Documents documents, String path) throws IOException {
        // TODO : 将每篇文档，每句话的主题都写出来
        BufferedWriter out = new BufferedWriter(new FileWriter(new File(path)));

        for (Document document : documents) {
            out.write(document.getName() + ": ,");
            for (Sentence sentence : document) {
                double[][] zr_prob = new double[Z][R];
                for (int i=0; i<Z; i++) {
                    for (int j=0; j<R; j++) {
                        zr_prob[i][j] = Math.log(theta[i]) + Math.log(pi[i][j]);
                        for (int word : sentence) {
                            zr_prob[i][j] = zr_prob[i][j] + Math.log(phi[i][j][word]);
                        }
                        zr_prob[i][j] = -zr_prob[i][j];
                    }
                }

                String zr = argsort(zr_prob)[0];
                int z = Integer.parseInt(zr.split(",")[0]);
                int r = Integer.parseInt(zr.split(",")[1]);

                out.write(z + "" + r + ",");
            }
            out.write("\n");
        }
    }

    public TMACE loadModel(String path) {
        return (TMACE) readObject(path);
    }

    public void fit(Documents documents, List<String> indexToWord, Map<String, Integer> bagOfWord, String savePath, String resultPath) throws IOException {
        this.init(documents, indexToWord, bagOfWord);
        this.inference(documents);
        this.updateParameter();
        this.saveModel(savePath);
        this.inferenceResult(documents, resultPath);
    }

    public static void main(String[] args) throws IOException {
        Date step0 = new Date();
        Documents documents = Util.loadData("resources/segmented/");
        Date step1 = new Date(); // loading data completed
        double time1 = (step1.getTime() - step0.getTime()) / 1000.0 / 60;
        System.out.println(time1);
        TMACE tmace = new TMACE(10, 0.1, 0.1, 0.3, 4, 2, 1000);
        tmace.fit(documents, Util.indexToWord, Util.bagOfWord, "resources/save/tmace.model", "resources/save/result.csv");
    }
}
