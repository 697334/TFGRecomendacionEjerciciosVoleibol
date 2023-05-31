package com.topic.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;

//import model.Perplexity;
import com.topic.utils.FileUtil;
import com.topic.utils.FuncUtils;

/**
 * ACA model for the competitive intelligence 
 */
public class ACAModel_GibbsSampling {
	//ϡ������ز���
	public double epsilon0;
	public double epsilon1;
	public double alpha0 ;  //������ alpha0 = 1E-12
	public double alpha1 ;  //������alpha1 = 0.1
	public double beta; // Hyper-parameter beta
	public double beta_back; //�����ʳ�����
	public double gamma; // Hyper-parameter gamma  Link�ʷֲ��Ĳ���
	public double gamma0;  //for beta distribution 
	double gamma1; //for beta distribution 
	public int K; // number of topics
	public int iterations; // number of Gibbs sampling iterations
	public Map<String, Integer> wordToIndexMap = new HashMap<String, Integer>();;  //word to index
	public List<String> indexToWordMap = new ArrayList<String>();    //index to String word 
	public int [][] docword;//word index array
	public int M; // number of documents in the corpus
	public int V; // number of words in the corpus
	public int[][] ndk; // document-topic count
	public int[] ndsum; //document-topic sum
	public int[][] nkw; //topic-word count
	public int[] nksum_w; //topic-word sum (total number of words assigned to a topic)
	public int[][] z_word; //topic assignment for word
	//for link LDA
	public Map<String, Integer> linkToIndexMap = new HashMap<String, Integer>(); //link to index
	public List<String> indexLinkMap = new ArrayList<String>();   //index to String link 
	public int [][] doclink;//link index array
	public int L; // number of links in the corpus
	public int[][] z_link;  //topic assignment for link
	public int[][] nkl;  //topic-link count
	public int[] nksum_l; //topic-link sum 
	//output
	public int topWordsAndLinksOutputNumber;
	public String outputFileDirectory; 
	public String outputFilecode; 
	boolean c[][]; //������ѡ����
	public long[] n_cv; //2ά��  ������0��Ӧ���ܵ�������  �� �Ǳ�����1��Ӧ���ܵ�������
	public int[] nback_v; //1*V ������ĳ���ʵ�Ƶ��
	double pi_b[];  //pi����
	boolean b[][]; //�ĵ�����ѡ����
	int b_sum[];  //�ĵ�����ĸ���
	JDKRandomGenerator rand; //�����������
	//������ѡ����

	public ACAModel_GibbsSampling(String inputFile, String inputFileCode, int topicNumber,
			double inputalpha0,double inputalpha1,double inputepsilon0,double inputepsilon1, double inputBeta,double inputBeta_back,double inputGamma,double inputGamma0,double inputGamma1, int inputIterations, int inTopWords,
			String outputFileDir){
		//read data
		ArrayList<String> docLines = new ArrayList<String>();
		FileUtil.readLines(inputFile, docLines,inputFileCode);
		M = docLines.size();
		docword = new int[M][];
		doclink = new int[M][];
		int j = 0;
		for(String line : docLines){
			List<String> words = new ArrayList<String>();
			List<String> links = new ArrayList<String>();
			FileUtil.tokenizeAndLowerCase(line.split("\t")[1], words);
			FileUtil.tokenizeEntity(line.split("\t")[0], links, line);
			docword[j] = new int[words.size()];
			for(int i = 0; i < words.size(); i++){
				String word = words.get(i);
				if(!wordToIndexMap.containsKey(word)){
					int newIndex = wordToIndexMap.size();
					wordToIndexMap.put(word, newIndex);
					indexToWordMap.add(word);
					docword[j][i] = newIndex;
				} else {
					docword[j][i] = wordToIndexMap.get(word);
				}
			}
			doclink[j] = new int[links.size()];
			for(int i = 0; i < links.size(); i++){
				String link = links.get(i);
				if(!linkToIndexMap.containsKey(link)){
					int newIndex = linkToIndexMap.size();
					linkToIndexMap.put(link, newIndex);
					indexLinkMap.add(link);
					doclink[j][i] = newIndex;
				} else {
					doclink[j][i] = linkToIndexMap.get(link);
				}
			}
			j++;

		}
		V = indexToWordMap.size();
		L = indexLinkMap.size();
		System.out.println("�ĵ���Ŀ:" + M + "\t" + "��������:" + V + "\t��������" + L  );
		alpha0 = inputalpha0;
		alpha1 = inputalpha1;
		epsilon0 = inputepsilon0;
		epsilon1 = inputepsilon1;
		beta = inputBeta;
		gamma = inputGamma;
		gamma0 = inputGamma0;
		gamma1 = inputGamma1;
		beta_back = inputBeta_back;
		K = topicNumber;
		iterations = inputIterations;
		topWordsAndLinksOutputNumber = inTopWords;
		outputFileDirectory = outputFileDir;
		outputFilecode = inputFileCode;
		initialize();
	}
	/**
	 * Randomly initialize topic assignments
	 */
	public void initialize(){
		rand = new JDKRandomGenerator();
		rand.setSeed(System.currentTimeMillis());
		//�����ֲ�����
		BetaDistribution betaDist = new BetaDistribution(epsilon0 , epsilon1);
		int D = docword.length;
		ndk = new int[D][K];
		ndsum = new int[D];
		nkw = new int[K][V];
		nksum_w = new int[K];
		z_word = new int[D][];
		z_link = new int[D][];
		nkl = new int[K][L];
		nksum_l = new int[K];
		//pi����
		pi_b = new double[M]; 
		//�ĵ�m�Ƿ��������k
		b = new boolean[M][K]; 
		//�ĵ�m�������������
		b_sum = new int[M]; 
		c = new boolean[M][];
		//�����ʺͷǱ���������ͳ��
		n_cv = new long[2];
		//����ͳ�� ĳ�����ڱ�����
		nback_v = new int[V];
		//��ȡ�ĵ�����ѡ����
		for (int d = 0; d < M; d++) {
			pi_b[d] = betaDist.sample();
			//�տ�ʼ��ʼ���ĵ�������������
			for (int k = 0; k < K; k++) {
				b[d][k] = true;
			}
			//�ĵ�������������Ŀ�ܺ�
			b_sum[d] = K;
		}

		for (int d = 0; d < D; d++) {
			int NWord = docword[d].length;  // the number of words in a document
			z_word[d] = new int[NWord];
			for (int n = 0; n < NWord; n++) {
				int topic = (int) (Math.random() * K);
				z_word[d][n] = topic;
//				updateCount(d, topic, docword[d][n], +1, 0);
			}
			//ȷ���ĵ�ÿ�����Ӷ�Ӧ������
			int NLink = doclink[d].length;
			z_link[d] = new int[NLink];
			for (int n = 0; n < NLink; n++) {
				int topic = (int) (Math.random() * K);
				z_link[d][n] = topic;
				updateCount(d, topic, doclink[d][n], +1, 1);
			}
		}
		//assign label c  --ȷ���ĵ�ÿ�����������Ա����ʻ������������
		for (int d = 0; d < M; d++) {
			c[d] = new boolean[docword[d].length];
			for (int n = 0; n < docword[d].length; n++) {
				if (Math.random() > 0.5) {
					c[d][n] = true;  //true��ʾ���Ǳ�����
					//����ͳ��
					updateCount(d, z_word[d][n], docword[d][n], +1, 0); //�ĵ������ͳ��
				} else {   //����Ǳ�����
					c[d][n] = false;
					updateCountBackWord(docword[d][n], 1);
				}
			}
		}
	}
	public void MCMCSampling(){
		for (int iter = 1; iter <= iterations; iter++) {
			long startTime=System.currentTimeMillis();
//			System.out.println("iteration : " + iter);
			gibbsOneIteration();
			long endTime=System.currentTimeMillis(); //��ȡ����ʱ��  
			System.out.println(iter + "\t" + (endTime-startTime) + "\t" +  "ms");   
		}
		// output the result
		System.out.println("write topic word ..." );
		writeTopWordsWithProbability();
		//		writeTopWords();
		System.out.println("write document topic ..." );
		writeDocumentTopic();
		System.out.println("write topic link ...");
		writeTopLinksWithProbability();
		//		writeTopLinks();
		System.out.println("write background topic ...");
		writeTopWordsWithProbability_Bar();
		System.out.println("write perplexity score ...");
		//writePerplexity();
	}
	public void gibbsOneIteration() {
		for (int d = 0; d < docword.length; d++) {
			//for words of this document
			for (int n = 0; n < z_word[d].length; n++) {
				//��ȡ���ʶ�Ӧ������
				int topic = sampleFullConditional(d, n);
				//�ȳ�ȡ����
				z_word[d][n] = topic;
				if (c[d][n]) {  //��ʾ���Ǳ�����
					ndk[d][topic] += 1;
					//�ĵ�d
					ndsum[d] += 1;
					//����topic��Ӧ�ĵ���word������1
					nkw[topic][docword[d][n]] += 1;
					//����topic��Ӧ�ĵ���������1
					nksum_w[topic] += 1;
				}
			}
			//for links of this document
			for (int n = 0; n < z_link[d].length; n++) {
				int topic = z_link[d][n]; // get the old topic
				updateCount(d, topic, doclink[d][n], -1, 1); // update the count --1
				double[] p = new double[K];
				for (int k = 0; k < K; k++) {
					int x = b[d][k] ? 1 : 0;
					p[k] = (ndk[d][k] + x*alpha1 + alpha0) / (ndsum[d] + b_sum[d]*alpha1 + K * alpha0) * (nkl[k][doclink[d][n]] + gamma)
							/ (nksum_l[k] + L * gamma);
				}
				topic = FuncUtils.rouletteGambling(p); //roulette gambling for updating the topic of a word
				z_link[d][n] = topic;
//				updateCount(d, topic, doclink[d][n], +1, 1);  // update the count ++1
			}
		}
		for (int d = 0; d < docword.length; d++) {
			//ѭ�����е���
			for (int n = 0; n < c[d].length; n++) {
				sample_label(d, n);
			}
		}
		//����ͳ����Ŀ
		cleanTempPrmts();
		for (int d = 0; d < docword.length; d++) {
			//ѭ�����е���
			for (int n = 0; n < c[d].length; n++) {
				if (c[d][n]) {
					//����ͳ��
					updateCount(d, z_word[d][n], docword[d][n], +1,0);
				}else {
					updateCountBackWord(docword[d][n], 1);
				}
			}
			for (int n = 0; n < z_link[d].length; n++) {
				updateCount(d, z_link[d][n], doclink[d][n], +1, 1); 
			}
		}
	}
	private void sample_label(int d, int n) {
		boolean binarylabel = c[d][n];
		int binary;
		if (binarylabel == true) {
			binary = 1;
		} else {
			binary = 0;
		}
		n_cv[binary]--;
		if (binary == 0) {  //����Ǳ�����
			nback_v[docword[d][n]]--;
		} else {   //������Ǳ�����
			ndk[d][z_word[d][n]]--;
			//�ĵ�d
			ndsum[d]--;
			//����topic��Ӧ�ĵ���word������1
			nkw[z_word[d][n]][docword[d][n]]--;
			//����topic��Ӧ�ĵ���������1
			nksum_w[z_word[d][n]]--;
		}
		binarylabel = draw_label(d, n);
		c[d][n] = binarylabel;
	}
	private boolean draw_label(int d, int n) {
		boolean returnvalue = false;
		double[] P_lv;
		P_lv = new double[2];
		double Pb = 1;
		double Ptopic = 1;

		P_lv[0] = (n_cv[0] + gamma0)
				/ (n_cv[0] + n_cv[1] + gamma0 + gamma1); // part 1 from

		P_lv[1] = (n_cv[1] + gamma1)
				/ (n_cv[0] + n_cv[1] + gamma0 + gamma1);

		Pb = (nback_v[docword[d][n]] + beta_back)
				/ (n_cv[0] + V*beta_back); // word in background part(2)
		Ptopic = (nkw[z_word[d][n]][docword[d][n]] + beta)
				/ (nksum_w[z_word[d][n]] + V*beta);

		double p0 = Pb * P_lv[0];
		double p1 = Ptopic * P_lv[1];

		double sum = p0 + p1;
		double randPick = Math.random();

		if (randPick <= p0 / sum) {
			returnvalue = false;
		} else {
			returnvalue = true;
		}
		return returnvalue;
	}
	//��ȡ���ʵ�����;�������Ϊ�ĵ�d�Լ��ĵ�d�еĵ���n
	int sampleFullConditional(int d, int n) {
		//��ȡԭ��Ӧ������
		int topic = z_word[d][n];
		if (c[d][n]) { //�����Ϊ������
			ndk[d][topic] += -1;
			//�ĵ�d
			ndsum[d] += -1;
			//����topic��Ӧ�ĵ���word������1
			nkw[topic][docword[d][n]] += -1;
			//����topic��Ӧ�ĵ���������1
			nksum_w[topic] += -1;
		}
		//����
		double[] p = new double[K];
		//ѭ��ÿ������
		for (int k = 0; k < K; k++) {
			int x = b[d][k] ? 1 : 0;
			p[k] = (ndk[d][k] + x*alpha1 + alpha0) / (ndsum[d] + b_sum[d]*alpha1 + K * alpha0) * (nkw[k][docword[d][n]] + beta)
					/ (nksum_w[k] + V * beta);
		}
		//���̶ĳ�ȡ������
		topic = sample(p);
		//��������
		return topic;

	}
	//��ȡʵ�������
	int sampleFullConditionalEntity(int d, int n) {

		int topic = z_link[d][n];

		updateCount(d, topic, doclink[d][n], -1, 1); ;

		double[] p = new double[K];

		for (int k = 0; k < K; k++) {
			int x = b[d][k] ? 1 : 0;
			p[k] = (ndk[d][k] + x*alpha1 + alpha0) / (ndsum[d] + b_sum[d]*alpha1 + K * alpha0) * (nkl[k][doclink[d][n]] + gamma)
					/ (nksum_l[k] + L * gamma);
		}
		topic = sample(p);

		//		updateCount(d, topic, doclink[d][n], +1, 1); ;

		return topic;

	}
	//���̶�
	int sample(double[] p) {

		int topic = 0;
		for (int k = 1; k < p.length; k++) {
			p[k] += p[k - 1];
		}
		double u = Math.random() * p[p.length - 1];
		for (int t = 0; t < p.length; t++) {
			if (u < p[t]) {
				topic = t;
				break;
			}
		}
		return topic;
	}
	/**
	 * update the count for word or link of assignment
	 * 
	 * @param d
	 * @return
	 */
	void updateCount(int d, int topic, int wordOrLink, int flagCount, int flagWordOrLink) {
		//word update
		if (flagWordOrLink == 0) {
			ndk[d][topic] += flagCount;
			ndsum[d] += flagCount;
			nkw[topic][wordOrLink] += flagCount;
			nksum_w[topic] += flagCount;
			n_cv[1] += flagCount; 
		}else {  //link update
			ndk[d][topic] += flagCount;
			ndsum[d] += flagCount;
			nkl[topic][wordOrLink] += flagCount;
			nksum_l[topic] += flagCount;
		}
	}
	//����ͳ��  ������
	void updateCountBackWord(int word, int flag) {
		nback_v[word] += flag;
		n_cv[0] += flag;  //��������Ŀͳ��
	}
	/**
	 * obtain the parameter Theta
	 */
	public double[][] estimateTheta() {
		double[][] theta = new double[docword.length][K];
		for (int d = 0; d < docword.length; d++) {
			for (int k = 0; k < K; k++) {
				int x = b[d][k] ? 1 : 0;
				theta[d][k] = (ndk[d][k] + + x*alpha1 + alpha0) / (ndsum[d] + b_sum[d]*alpha1 + K * alpha0);
			}
		}
		return theta;
	}
	public void cleanTempPrmts() {
		ndk = new int[M][K];
		ndsum = new int[M];
		//����k�е���v����Ŀ
		nkw = new int[K][V];
		//����k��Ӧ�ĵ�������
		nksum_w = new int[K];
		//ÿƪ�ĵ����ʶ�Ӧ������
		n_cv = new long[2];
		//����ͳ�� ĳ�����ڱ�����
		nback_v = new int[V];
		nkl = new int[K][L];
		nksum_l = new int[K];
	}
	/**
	 * obtain the parameter Phi for words
	 */
	public double[][] estimatePhi_word() {
		double[][] phi = new double[K][V];
		for (int k = 0; k < K; k++) {
			for (int w = 0; w < V; w++) {
				phi[k][w] = (nkw[k][w] + beta) / (nksum_w[k] + V * beta);
			}
		}
		return phi;
	}
	/**
	 * obtain the parameter Phi for words
	 */
	public double[][] estimatePhi_link() {
		double[][] phi = new double[K][L];
		for (int k = 0; k < K; k++) {
			for (int l = 0; l < L; l++) {
				phi[k][l] = (nkl[k][l] + gamma) / (nksum_l[k] + L * gamma);
			}
		}
		return phi;
	}
	//����Phi
	public double[] estimatePhi_Bar() {
		double[] phi_bar = new double[V];
		for (int w = 0; w < V; w++) {
			phi_bar[w] =  (nback_v[w] + beta_back)
					/ (n_cv[0] + V*beta_back);;
		}
		return phi_bar;
	}
	/**
	 * write top words with probability for background  topic
	 */
	public void writeTopWordsWithProbability_Bar(){
		StringBuilder sBuilder = new StringBuilder();
		double[] phi_bar = estimatePhi_Bar();
		sBuilder.append("Background Topic: \n");
		for (int i = 0; i < topWordsAndLinksOutputNumber; i++) {
			int max_index = FuncUtils.maxValueIndex(phi_bar);
			sBuilder.append(indexToWordMap.get(max_index) + " :" + phi_bar[max_index] + "\n");
			phi_bar[max_index] = 0;
		}
		try {
			FileUtil.writeFile(outputFileDirectory + "SparseBLDA_backgroundtopic_word_" + K + ".txt", sBuilder.toString(),outputFilecode);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * write top words with probability for each topic
	 */
	public void writeTopWordsWithProbability(){
		StringBuilder sBuilder = new StringBuilder();
		double[][] phi = estimatePhi_word();
		int topicNumber = 1;
		for (double[] phi_z : phi) {
			sBuilder.append("Topic:" + topicNumber + "\n");
			for (int i = 0; i < topWordsAndLinksOutputNumber; i++) {
				int max_index = FuncUtils.maxValueIndex(phi_z);
				sBuilder.append(indexToWordMap.get(max_index) + " :" + phi_z[max_index] + "\n");
				phi_z[max_index] = 0;
			}
			sBuilder.append("\n");
			topicNumber++;
		}
		try {
			FileUtil.writeFile(outputFileDirectory + "SparseBLDA_topic_word_" + K + ".txt", sBuilder.toString(),outputFilecode);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * write top words for each topic
	 */
	public void writeTopWords(){
		StringBuilder sBuilder = new StringBuilder();
		double[][] phi = estimatePhi_word();
		for (double[] phi_z : phi) {
			for (int i = 0; i < topWordsAndLinksOutputNumber; i++) {
				int max_index = FuncUtils.maxValueIndex(phi_z);
				sBuilder.append(indexToWordMap.get(max_index) + "\t");
				phi_z[max_index] = 0;
			}
			sBuilder.append("\n");
		}
		try {
			FileUtil.writeFile(outputFileDirectory + "SparseBLDA_topic_wordnop_" + K + ".txt", sBuilder.toString(),outputFilecode);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * write top words with probability for each topic
	 */
	public void writeTopLinksWithProbability(){
		StringBuilder sBuilder = new StringBuilder();
		double[][] phi = estimatePhi_link();
		int topicNumber = 1;
		for (double[] phi_z : phi) {
			sBuilder.append("Topic:" + topicNumber + "\n");
			for (int i = 0; i < topWordsAndLinksOutputNumber; i++) {
				int max_index = FuncUtils.maxValueIndex(phi_z);
				sBuilder.append(indexLinkMap.get(max_index) + " :" + phi_z[max_index] + "\n");
				phi_z[max_index] = 0;
			}
			sBuilder.append("\n");
			topicNumber++;
		}
		try {
			FileUtil.writeFile(outputFileDirectory + "SparseBLDA_topic_link_" + K + ".txt", sBuilder.toString(),outputFilecode);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * write top words for each topic
	 */
	public void writeTopLinks(){
		StringBuilder sBuilder = new StringBuilder();
		double[][] phi = estimatePhi_link();
		for (double[] phi_z : phi) {
			for (int i = 0; i < topWordsAndLinksOutputNumber; i++) {
				int max_index = FuncUtils.maxValueIndex(phi_z);
				sBuilder.append(indexLinkMap.get(max_index) + "\t");
				phi_z[max_index] = 0;
			}
			sBuilder.append("\n");
		}
		try {
			FileUtil.writeFile(outputFileDirectory + "SparseBLDA_topic_linknop_" + K + ".txt", sBuilder.toString(),outputFilecode);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * write theta for each document
	 */
	/*public void writePerplexity(){
		StringBuilder sBuilder = new StringBuilder();
		double[][] theta = estimateTheta();
		double[][] phi = estimatePhi_word();
		double perplexity = Perplexity.lda_training_perplexity(docword, theta, phi);
		sBuilder.append(K + "\t Perplexity is: \n");
		sBuilder.append(perplexity);
		try {
			FileUtil.writeFile(outputFileDirectory + "SparseBLDA_perplexity" + K + ".txt", sBuilder.toString(),outputFilecode);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
	/**
	 * write theta for each document
	 */
	public void writeDocumentTopic(){
		double[][] theta = estimateTheta();
		StringBuilder sBuilder = new StringBuilder();
		for (int d = 0; d < theta.length; d++) {
			StringBuilder doc = new StringBuilder();
			for (int k = 0; k < theta[d].length; k++) {
				doc.append(theta[d][k] + "\t");
			}
			sBuilder.append(doc.toString().trim() + "\n");
		}
		try {
			FileUtil.writeFile(outputFileDirectory + "SparseBLDA_doc_topic_" + K + ".txt", sBuilder.toString(),outputFilecode);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String args[]) throws Exception{
	
		ACAModel_GibbsSampling aca = new ACAModel_GibbsSampling("cardata/rawdata_process",
				"gbk", 40, 1E-12,0.1,0.1,0.1,
				0.01,0.01,0.01, 0.1,0.1,1000, 100, "cardata/result/");
		aca.MCMCSampling();
	}
}
