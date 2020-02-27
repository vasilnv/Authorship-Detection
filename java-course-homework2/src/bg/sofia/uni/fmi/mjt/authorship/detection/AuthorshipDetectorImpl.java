package bg.sofia.uni.fmi.mjt.authorship.detection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class AuthorshipDetectorImpl implements AuthorshipDetector {
	private static final int NULLA = 0;
	private static final int ONE = 1;
	private static final int TWO = 2;
	private static final int THREE = 3;
	private static final int FOUR = 4;
	private static final int FIVE = 5;

	private double[] weights;
	private Map<String, LinguisticSignature> authors;
	private Map<LinguisticSignature, String> signatures;

	public AuthorshipDetectorImpl(InputStream signaturesDataset, double[] weights) {
		this.weights = weights;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(signaturesDataset))) {
			authors = new HashMap<>();
			signatures = new HashMap<>();

			String line = br.readLine();
			while (line != null) {
				String[] authorsSigns = line.split(",");
				String currAuthor = authorsSigns[NULLA];
				Map<FeatureType, Double> features = new HashMap<>();
				features.put(FeatureType.AVERAGE_WORD_LENGTH, Double.parseDouble(authorsSigns[ONE]));
				features.put(FeatureType.TYPE_TOKEN_RATIO, Double.parseDouble(authorsSigns[TWO]));
				features.put(FeatureType.HAPAX_LEGOMENA_RATIO, Double.parseDouble(authorsSigns[THREE]));
				features.put(FeatureType.AVERAGE_SENTENCE_LENGTH, Double.parseDouble(authorsSigns[FOUR]));
				features.put(FeatureType.AVERAGE_SENTENCE_COMPLEXITY, Double.parseDouble(authorsSigns[FIVE]));

				LinguisticSignature currSignature = new LinguisticSignature(features);
				authors.put(currAuthor, currSignature);
				signatures.put(currSignature, currAuthor);
				line = br.readLine();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String cleanUp(String word) {
		return word.toLowerCase().replaceAll(
				"^[!.,:;\\-?<>#\\*\'\"\\[\\(\\]\\)\\n\\t\\\\]+|[!.,:;\\-?<>#\\*\'\"\\[\\(\\]\\)\\n\\t\\\\]+$", "");
	}

	@Override
	public LinguisticSignature calculateSignature(InputStream mysteryText) {
		if (mysteryText == null) {
			throw new IllegalArgumentException();
		}
		Map<String, Integer> words = new HashMap<>();
		StringBuilder sb = new StringBuilder();
		LinguisticSignature currSign = null;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(mysteryText))) {
			String line = br.readLine();
			Map<FeatureType, Double> features = new HashMap<>();
			double wordsLength = NULLA;
			double allWords = NULLA;
			double cntForPhrases = NULLA;
			while (line != null) {
				sb.append(line);
				String[] wordsOnLine = line.split("\\s+");
				allWords += wordsOnLine.length;
				for (int i = 0; i < wordsOnLine.length; i++) {
					wordsOnLine[i] = cleanUp(wordsOnLine[i]);
					wordsLength += wordsOnLine[i].length();
				}
				for (int i = 0; i < wordsOnLine.length; i++) {
					if (!words.containsKey(wordsOnLine[i])) {
						words.put(wordsOnLine[i], NULLA);
					} else {
						words.put(wordsOnLine[i], words.get(wordsOnLine[i]) + ONE);
					}
				}
				line = br.readLine();
			}
			String[] sentences = sb.toString().split("[!.?]");
			double numOfSentences = sentences.length;
			for (int i = 0; i < numOfSentences; i++) {
				if (sentences[i].contains(",") || sentences[i].contains(":") || sentences[i].contains(";")) {
					String[] phrases = sentences[i].split("[,:;]");
					cntForPhrases += phrases.length;
				}
			}
			double sizeOf1s = words.entrySet().stream().filter(e -> e.getValue() == NULLA).count();
			features.put(FeatureType.TYPE_TOKEN_RATIO, (double) (words.size()) / allWords);
			features.put(FeatureType.HAPAX_LEGOMENA_RATIO, sizeOf1s / allWords);
			features.put(FeatureType.AVERAGE_WORD_LENGTH, wordsLength / allWords);
			features.put(FeatureType.AVERAGE_SENTENCE_LENGTH, (double) (allWords) / numOfSentences);
			features.put(FeatureType.AVERAGE_SENTENCE_COMPLEXITY, cntForPhrases / numOfSentences);
			currSign = new LinguisticSignature(features);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return currSign;
	}

	@Override
	public double calculateSimilarity(LinguisticSignature firstSignature, LinguisticSignature secondSignature) {
		if (firstSignature == null || secondSignature == null) {
			throw new IllegalArgumentException();
		}
		double sum = 0;
		sum += Math.abs(firstSignature.getFeatures().get(FeatureType.AVERAGE_WORD_LENGTH)
				- secondSignature.getFeatures().get(FeatureType.AVERAGE_WORD_LENGTH)) * weights[NULLA];
		sum += Math.abs(firstSignature.getFeatures().get(FeatureType.TYPE_TOKEN_RATIO)
				- secondSignature.getFeatures().get(FeatureType.TYPE_TOKEN_RATIO)) * weights[ONE];
		sum += Math.abs(firstSignature.getFeatures().get(FeatureType.HAPAX_LEGOMENA_RATIO)
				- secondSignature.getFeatures().get(FeatureType.HAPAX_LEGOMENA_RATIO)) * weights[TWO];
		sum += Math.abs(firstSignature.getFeatures().get(FeatureType.AVERAGE_SENTENCE_LENGTH)
				- secondSignature.getFeatures().get(FeatureType.AVERAGE_SENTENCE_LENGTH)) * weights[THREE];
		sum += Math.abs(firstSignature.getFeatures().get(FeatureType.AVERAGE_SENTENCE_COMPLEXITY)
				- secondSignature.getFeatures().get(FeatureType.AVERAGE_SENTENCE_COMPLEXITY)) * weights[FOUR];
		return sum;
	}

	@Override
	public String findAuthor(InputStream mysteryText) {
		if (mysteryText == null) {
			throw new IllegalArgumentException();
		}
		LinguisticSignature mysteryAuthorSign = calculateSignature(mysteryText);
		String res = "";
		double best = Double.MAX_VALUE;
		for (Map.Entry<LinguisticSignature, String> entry : signatures.entrySet()) {
			double curr = calculateSimilarity(entry.getKey(), mysteryAuthorSign);
			if (curr < best) {
				best = curr;
				res = entry.getValue();
			}
		}
		return res;
	}

}
