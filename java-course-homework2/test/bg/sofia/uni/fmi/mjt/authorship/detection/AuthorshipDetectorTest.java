package bg.sofia.uni.fmi.mjt.authorship.detection;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AuthorshipDetectorTest {
	private static final double ELEV = 11;
	private static final double THIRTYTHREE = 33;
	private static final double FIFTY = 50;
	private static final double POINT_FOUR = 0.4;
	private static final double FOUR = 4;

	private static final double FIVE_POINT_ONE = 5.1;
	private static final double POINT_NINE = 0.9;
	private static final double POINT_EIGHT = 0.8;
	private static final double FIVE = 5.0;
	private static final double TWO = 2.0;

	private String textString;
	private String authorsString;

	@Before
	public void init() {
		textString = "Emma Woodhouse, handsome, clever, and rich. With a handsome home\r\n";
		authorsString = "Agatha Christie, 4.40212537354, 0.103719383127, 0.0534892315963, "
				+ "10.0836888743, 1.90662947161\r\n"
				+ "Alexandre Dumas, 4.38235547477, 0.049677588873, 0.0212183996175, 15.0054854981, 2.63499369483\r\n"
				+ "Brothers Grim, 3.96868608302, 0.0529378997714, 0.0208217283571, 22.2267197987, 3.4129614094\r\n"
				+ "Charles Dickens, 4.34760725241, 0.0803220950584, 0.0390662700499, 16.2613453121, 2.87721723105\r\n"
				+ "Douglas Adams, 4.33408042189, 0.238435104414, 0.141554321967, 13.2874354561, 1.86574870912\r\n"
				+ "Fyodor Dostoevsky, 4.34066732195, 0.0528571428571, 0.0233414043584, 12.8108273249,"
				+ " 2.16705364781\r\n"
				+ "James Joyce, 4.52346300961, 0.120109917189, 0.0682315429476, 10.9663296918, 1.79667373227\r\n"
				+ "Jane Austen, 5.1, 0.9, 0.8, 5.0, 2.0\r\n"
				+ "Lewis Carroll, 4.22709528497, 0.111591342227, 0.0537026953444, 16.2728740581, 2.86275565124\r\n"
				+ "Mark Twain, 4.33272222298, 0.117254215021, 0.0633074228159, 14.3548573631, 2.43716268311\r\n"
				+ "Sir Arthur Conan Doyle, 4.16808311494, 0.0822989796874, 0.0394458485444, 14.717564466, "
				+ "2.2220872148\r\n"
				+ "William Shakespeare, 4.16216957834, 0.105602561171, 0.0575348730848, 9.34707371975, 2.24620146314";
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIfCalculateThrowsException() {
		InputStream authorsStream = new ByteArrayInputStream(authorsString.getBytes());
		InputStream textStream = new ByteArrayInputStream(textString.getBytes());

		double[] weights = { ELEV, THIRTYTHREE, FIFTY, POINT_FOUR, FOUR };
		AuthorshipDetector detector = new AuthorshipDetectorImpl(authorsStream, weights);
		LinguisticSignature ls1 = detector.calculateSignature(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIfFindAuthorThrowsException() {
		InputStream authorsStream = new ByteArrayInputStream(authorsString.getBytes());
		InputStream textStream = new ByteArrayInputStream(textString.getBytes());

		double[] weights = { ELEV, THIRTYTHREE, FIFTY, POINT_FOUR, FOUR };
		AuthorshipDetector detector = new AuthorshipDetectorImpl(authorsStream, weights);
		detector.findAuthor(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIfCalculateSimilarityThrowsException() {
		double[] weights = { ELEV, THIRTYTHREE, FIFTY, POINT_FOUR, FOUR };
		InputStream authorsStream = new ByteArrayInputStream(authorsString.getBytes());
		InputStream textStream = new ByteArrayInputStream(textString.getBytes());
		AuthorshipDetector detector = new AuthorshipDetectorImpl(authorsStream, weights);
		LinguisticSignature ls1 = detector.calculateSignature(textStream);
		Map<FeatureType, Double> features = new HashMap<>();
		features.put(FeatureType.AVERAGE_WORD_LENGTH, FIVE_POINT_ONE);
		features.put(FeatureType.TYPE_TOKEN_RATIO, POINT_NINE);
		features.put(FeatureType.HAPAX_LEGOMENA_RATIO, POINT_EIGHT);
		features.put(FeatureType.AVERAGE_SENTENCE_LENGTH, FIVE);
		features.put(FeatureType.AVERAGE_SENTENCE_COMPLEXITY, TWO);
		LinguisticSignature ls2 = new LinguisticSignature(features);
		detector.calculateSimilarity(null, ls2);

	}

	@Test
	public void testCalculationWordLengthFeature() throws FileNotFoundException {
		InputStream authorsStream = new ByteArrayInputStream(authorsString.getBytes());
		InputStream textStream = new ByteArrayInputStream(textString.getBytes());

		double[] weights = { ELEV, THIRTYTHREE, FIFTY, POINT_FOUR, FOUR };
		AuthorshipDetector detector = new AuthorshipDetectorImpl(authorsStream, weights);
		LinguisticSignature ls1 = detector.calculateSignature(textStream);
		Map<FeatureType, Double> features = new HashMap<>();
		features.put(FeatureType.AVERAGE_WORD_LENGTH, FIVE_POINT_ONE);
		features.put(FeatureType.TYPE_TOKEN_RATIO, POINT_NINE);
		features.put(FeatureType.HAPAX_LEGOMENA_RATIO, POINT_EIGHT);
		features.put(FeatureType.AVERAGE_SENTENCE_LENGTH, FIVE);
		features.put(FeatureType.AVERAGE_SENTENCE_COMPLEXITY, TWO);
		LinguisticSignature ls2 = new LinguisticSignature(features);

		Assert.assertEquals(ls2.getFeatures().get(FeatureType.AVERAGE_WORD_LENGTH),
				ls1.getFeatures().get(FeatureType.AVERAGE_WORD_LENGTH));

	}

	@Test
	public void testCalculationTypeTokenRatioFeature() throws FileNotFoundException {
		InputStream authorsStream = new ByteArrayInputStream(authorsString.getBytes());
		InputStream textStream = new ByteArrayInputStream(textString.getBytes());

		double[] weights = { ELEV, THIRTYTHREE, FIFTY, POINT_FOUR, FOUR };
		AuthorshipDetector detector = new AuthorshipDetectorImpl(authorsStream, weights);
		LinguisticSignature ls1 = detector.calculateSignature(textStream);
		Map<FeatureType, Double> features = new HashMap<>();
		features.put(FeatureType.AVERAGE_WORD_LENGTH, FIVE_POINT_ONE);
		features.put(FeatureType.TYPE_TOKEN_RATIO, POINT_NINE);
		features.put(FeatureType.HAPAX_LEGOMENA_RATIO, POINT_EIGHT);
		features.put(FeatureType.AVERAGE_SENTENCE_LENGTH, FIVE);
		features.put(FeatureType.AVERAGE_SENTENCE_COMPLEXITY, TWO);
		LinguisticSignature ls2 = new LinguisticSignature(features);

		Assert.assertEquals(ls2.getFeatures().get(FeatureType.TYPE_TOKEN_RATIO),
				ls1.getFeatures().get(FeatureType.TYPE_TOKEN_RATIO));

	}

	@Test
	public void testCalculationHapaxLegomenaRatioFeature() throws FileNotFoundException {
		InputStream authorsStream = new ByteArrayInputStream(authorsString.getBytes());
		InputStream textStream = new ByteArrayInputStream(textString.getBytes());

		double[] weights = { ELEV, THIRTYTHREE, FIFTY, POINT_FOUR, FOUR };
		AuthorshipDetector detector = new AuthorshipDetectorImpl(authorsStream, weights);
		LinguisticSignature ls1 = detector.calculateSignature(textStream);
		Map<FeatureType, Double> features = new HashMap<>();
		features.put(FeatureType.AVERAGE_WORD_LENGTH, FIVE_POINT_ONE);
		features.put(FeatureType.TYPE_TOKEN_RATIO, POINT_NINE);
		features.put(FeatureType.HAPAX_LEGOMENA_RATIO, POINT_EIGHT);
		features.put(FeatureType.AVERAGE_SENTENCE_LENGTH, FIVE);
		features.put(FeatureType.AVERAGE_SENTENCE_COMPLEXITY, TWO);
		LinguisticSignature ls2 = new LinguisticSignature(features);

		Assert.assertEquals(ls2.getFeatures().get(FeatureType.HAPAX_LEGOMENA_RATIO),
				ls1.getFeatures().get(FeatureType.HAPAX_LEGOMENA_RATIO));

	}

	@Test
	public void testCalculationAverageSentenceLebgthFeature() throws FileNotFoundException {
		InputStream authorsStream = new ByteArrayInputStream(authorsString.getBytes());
		InputStream textStream = new ByteArrayInputStream(textString.getBytes());

		double[] weights = { ELEV, THIRTYTHREE, FIFTY, POINT_FOUR, FOUR };
		AuthorshipDetector detector = new AuthorshipDetectorImpl(authorsStream, weights);
		LinguisticSignature ls1 = detector.calculateSignature(textStream);
		Map<FeatureType, Double> features = new HashMap<>();
		features.put(FeatureType.AVERAGE_WORD_LENGTH, FIVE_POINT_ONE);
		features.put(FeatureType.TYPE_TOKEN_RATIO, POINT_NINE);
		features.put(FeatureType.HAPAX_LEGOMENA_RATIO, POINT_EIGHT);
		features.put(FeatureType.AVERAGE_SENTENCE_LENGTH, FIVE);
		features.put(FeatureType.AVERAGE_SENTENCE_COMPLEXITY, TWO);
		LinguisticSignature ls2 = new LinguisticSignature(features);

		Assert.assertEquals(ls2.getFeatures().get(FeatureType.AVERAGE_SENTENCE_LENGTH),
				ls1.getFeatures().get(FeatureType.AVERAGE_SENTENCE_LENGTH));

	}

	@Test
	public void testCalculationAverageSentenceComplexityFeature() throws FileNotFoundException {
		InputStream authorsStream = new ByteArrayInputStream(authorsString.getBytes());
		InputStream textStream = new ByteArrayInputStream(textString.getBytes());

		double[] weights = { ELEV, THIRTYTHREE, FIFTY, POINT_FOUR, FOUR };
		AuthorshipDetector detector = new AuthorshipDetectorImpl(authorsStream, weights);
		LinguisticSignature ls1 = detector.calculateSignature(textStream);
		Map<FeatureType, Double> features = new HashMap<>();
		features.put(FeatureType.AVERAGE_WORD_LENGTH, FIVE_POINT_ONE);
		features.put(FeatureType.TYPE_TOKEN_RATIO, POINT_NINE);
		features.put(FeatureType.HAPAX_LEGOMENA_RATIO, POINT_EIGHT);
		features.put(FeatureType.AVERAGE_SENTENCE_LENGTH, FIVE);
		features.put(FeatureType.AVERAGE_SENTENCE_COMPLEXITY, TWO);
		LinguisticSignature ls2 = new LinguisticSignature(features);

		Assert.assertEquals(ls2.getFeatures().get(FeatureType.AVERAGE_SENTENCE_COMPLEXITY),
				ls1.getFeatures().get(FeatureType.AVERAGE_SENTENCE_COMPLEXITY));

	}

	@Test
	public void testSimilarity() throws FileNotFoundException {
		InputStream authorsStream = new ByteArrayInputStream(authorsString.getBytes());

		double[] weights = { ELEV, THIRTYTHREE, FIFTY, POINT_FOUR, FOUR };
		InputStream textStream = new ByteArrayInputStream(textString.getBytes());
		AuthorshipDetector detector = new AuthorshipDetectorImpl(authorsStream, weights);
		LinguisticSignature ls1 = detector.calculateSignature(textStream);
		Map<FeatureType, Double> features = new HashMap<>();
		features.put(FeatureType.AVERAGE_WORD_LENGTH, FIVE_POINT_ONE);
		features.put(FeatureType.TYPE_TOKEN_RATIO, POINT_NINE);
		features.put(FeatureType.HAPAX_LEGOMENA_RATIO, POINT_EIGHT);
		features.put(FeatureType.AVERAGE_SENTENCE_LENGTH, FIVE);
		features.put(FeatureType.AVERAGE_SENTENCE_COMPLEXITY, TWO);
		LinguisticSignature ls2 = new LinguisticSignature(features);
		double res = detector.calculateSimilarity(ls1, ls2);
		Assert.assertEquals(0, res, 0.0);
	}

	@Test
	public void testFindAuthors() {
		InputStream authorsStream = new ByteArrayInputStream(authorsString.getBytes());
		double[] weights = { ELEV, THIRTYTHREE, FIFTY, POINT_FOUR, FOUR };
		InputStream textStream = new ByteArrayInputStream(textString.getBytes());
		AuthorshipDetector detector = new AuthorshipDetectorImpl(authorsStream, weights);
		String res = detector.findAuthor(textStream);
		Assert.assertEquals("Jane Austen", res);

	}

}
