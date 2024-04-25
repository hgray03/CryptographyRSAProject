import java.io.IOException;
import java.math.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RSA2048 {

	public static void main(String[] args) { // this was not the best way of implementing the sending and receiving of
												// messages, but it works!
		// I also use the Path's in my computer... should've thought about that in advance.
		// I will happily demo my code to prove it works.

		String textInputMatt = "/Users/harrisongray/Desktop/CryptographyRSA/mattinput.txt";
		String textInputBrian = "/Users/harrisongray/Desktop/CryptographyRSA/brianinput.txt";
		String textInputBrian2 = "/Users/harrisongray/Desktop/CryptographyRSA/brianinput2.txt";
		String textInputBrian3 = "/Users/harrisongray/Desktop/CryptographyRSA/brianinput3.txt";

		String pInput = "/Users/harrisongray/Desktop/CryptographyRSA/p.txt";
		String qInput = "/Users/harrisongray/Desktop/CryptographyRSA/q.txt"; //
		String nInput = "/Users/harrisongray/Desktop/CryptographyRSA/n.txt";
		String eInput = "/Users/harrisongray/Desktop/CryptographyRSA/e.txt";
		String outputMatt = ""; // in order to do each block you need to run the program with a different input
								// file,
		String outputBrian = "";
		String outputBrian2 = "";
		String outputBrian3 = "";
		try {
			BigInteger inputCipherTextMatt = new BigInteger(new String(Files.readAllBytes(Paths.get(textInputMatt))));
			BigInteger inputCipherTextBrian = new BigInteger(new String(Files.readAllBytes(Paths.get(textInputBrian))));
			BigInteger inputCipherTextBrian2 = new BigInteger(
					new String(Files.readAllBytes(Paths.get(textInputBrian2))));
			BigInteger inputCipherTextBrian3 = new BigInteger(
					new String(Files.readAllBytes(Paths.get(textInputBrian3))));

			BigInteger p = new BigInteger(new String(Files.readAllBytes(Paths.get(pInput))));
			BigInteger q = new BigInteger(new String(Files.readAllBytes(Paths.get(qInput))));
			BigInteger n = new BigInteger(new String(Files.readAllBytes(Paths.get(nInput))));
			BigInteger nPhi = generatePhi(p, q);
			BigInteger e = new BigInteger(new String(Files.readAllBytes(Paths.get(eInput))));
			BigInteger d = GenerateD(p, q, nPhi, e);
			outputMatt = convertBigIntToString(decrypt(n, d, inputCipherTextMatt));
			outputBrian = convertBigIntToString(decrypt(n, d, inputCipherTextBrian));
			outputBrian2 = convertBigIntToString(decrypt(n, d, inputCipherTextBrian2));
			outputBrian3 = convertBigIntToString(decrypt(n, d, inputCipherTextBrian3));

			System.out.println("Matthew's Message to me: " + outputMatt);
			System.out.println();
			System.out.println("Brian's Message to me: " + outputBrian + " " + outputBrian2 + " " + outputBrian3);

		} catch (

		IOException e) {
			e.printStackTrace();
		}

		String nInputClassMate = "/Users/harrisongray/Desktop/CryptographyRSA/n2.txt"; // n2 is Brian's n
		String eInputClassMate = "/Users/harrisongray/Desktop/CryptographyRSA/e2.txt"; // e 2 is Brian's e
		String messageInputClassMate = "/Users/harrisongray/Desktop/CryptographyRSA/messageToBrian.txt"; // if you want
																											// to switch
																											// the text
																											// file to
																											// "messageToMatt.txt",
																											// then the
																											// n2, and
																											// e2 have
																											// to be
																											// switch to
																											// n1 and
																											// e1. (n1
																											// and e1
																											// are
																											// Matt's n
																											// and e
																											// values)
		String inputMessageClassMate = "";

		try {
			inputMessageClassMate = new String(Files.readAllBytes(Paths.get(messageInputClassMate)));
			BigInteger n = new BigInteger(new String(Files.readAllBytes(Paths.get(nInputClassMate))));
			BigInteger e = new BigInteger(new String(Files.readAllBytes(Paths.get(eInputClassMate))));
			List<String> tempList = splitIntoChunks(inputMessageClassMate, 214);
			System.out.println("encrypted message to Brian in chunks: ");
			for (int i = 0; i < tempList.size(); i++) {
				String message = tempList.get(i);
				BigInteger m = convertStringToBigInt(message);
				BigInteger c = encrypt(n, e, m);
				System.out.println(c);
				System.out.println();
			}
		} catch (
		IOException e1) {
			e1.printStackTrace();
		}
		BigInteger p = solovayStrassen();
		BigInteger q = solovayStrassen();
		BigInteger n = generateKey(p, q); // test however you like, I just thought I should provide these
		BigInteger nPhi = generatePhi(p, q);
		BigInteger e = generateE(nPhi);
		BigInteger d = GenerateD(p, q, nPhi, e);
		System.out.println(

				decrypt(n, d, encrypt(n, e, BigInteger.valueOf(1234)/* <--- integer representation of message */)));

	}

	public static BigInteger solovayStrassen() {
		boolean isPrime = false;
		int nCounter = 0;
		int nProbabilityPrime = 0;
		int Confidence = 21;
		BigInteger n;
		BigInteger a;
		do {
			isPrime = false;
			Random randNum = new Random();
			n = (new BigInteger(1024, randNum)).add(BigInteger.ONE);
			if (n.testBit(0) == false) {
				n = n.add(BigInteger.ONE);
			}
			boolean isComposite = false;
			while (isComposite == false && nProbabilityPrime < Confidence) {
				a = (new BigInteger(64, randNum)).add(BigInteger.ONE);
				if (a.gcd(n).equals(BigInteger.ONE)) {
					BigInteger nExponent = n.subtract(BigInteger.ONE);
					nExponent = nExponent.divide(BigInteger.TWO);
					BigInteger compositeCheck = a.modPow(nExponent, n);
					if ((compositeCheck.equals(BigInteger.ONE)) || compositeCheck.equals(n.subtract(BigInteger.ONE))) { // problem
						BigInteger jacobiResult = jacobi(a, n);
						if (compositeCheck.equals(jacobiResult) || (jacobiResult.equals(BigInteger.valueOf(-1))
								&& compositeCheck.equals(n.subtract(BigInteger.ONE)))) {
							nProbabilityPrime++;

						} else {
							isComposite = true;
							isPrime = false;
							nProbabilityPrime = 0;
						}
					} else {
						isPrime = false;
						isComposite = true;
						nProbabilityPrime = 0;
					}
				} else {
					isComposite = true;
					isPrime = false;
					nProbabilityPrime = 0;
				}
			} // is composite loop
			nCounter++;

		} while (isPrime == false && nProbabilityPrime < Confidence);
		float probability = (float) (1 - probability(Confidence));
		System.out.println("Confidence: " + probability);
		System.out.println("Number of values used to verify: " + Confidence);
		System.out.println("number of tries: " + nCounter);
		System.out.println("prime number: " + n);
		return n;

	}

	public static BigInteger jacobi(BigInteger a, BigInteger b) {
		BigInteger two = BigInteger.valueOf(2);
		BigInteger four = BigInteger.valueOf(4);
		BigInteger eight = BigInteger.valueOf(8);

		if (b.compareTo(BigInteger.ZERO) <= 0 || !b.mod(two).equals(BigInteger.ONE)) {
			throw new IllegalArgumentException("b must be an odd positive integer.");
		}
		if (!a.gcd(b).equals(BigInteger.ONE)) {
			throw new IllegalArgumentException("a and b gcd is not 1");
		}

		int j = 1;
		if (a.compareTo(BigInteger.ZERO) < 0) {
			a = a.negate();
			if (b.mod(four).equals(BigInteger.valueOf(3))) {
				j = -j;
			}
		}
		while (!a.equals(BigInteger.ZERO)) {
			while (a.mod(two).equals(BigInteger.ZERO)) {
				a = a.divide(two);
				if (b.mod(eight).equals(BigInteger.valueOf(3)) || b.mod(eight).equals(BigInteger.valueOf(5))) {
					j = -j;
				}
			}
			BigInteger temp = a;
			a = b;
			b = temp;
			if (a.mod(four).equals(BigInteger.valueOf(3)) && b.mod(four).equals(BigInteger.valueOf(3))) {
				j = -j;
			}
			a = a.mod(b);
		}
		if (b.equals(BigInteger.ONE)) {
			return BigInteger.valueOf(j);
		}
		return BigInteger.ZERO;
	}

	public static BigInteger generateKey(BigInteger p, BigInteger q) {
		BigInteger n = p.multiply(q);
		return n;

	}

	public static BigInteger generatePhi(BigInteger p, BigInteger q) {
		BigInteger p1 = p.subtract(BigInteger.ONE);
		BigInteger q1 = q.subtract(BigInteger.ONE);
		BigInteger nPhi = p1.multiply(q1);
		return nPhi;
	}

	public static BigInteger generateE(BigInteger nPhi) {
		BigInteger e;
		do {
			Random randNum = new Random();
			e = new BigInteger(2047, randNum);

		} while (!e.gcd(nPhi).equals(BigInteger.ONE));
		return e;

	}

	public static BigInteger GenerateD(BigInteger p, BigInteger q, BigInteger nPhi, BigInteger e) {
		BigInteger d = inverseMod(e, nPhi);
		return d;
	}

	public static BigInteger inverseMod(BigInteger m, BigInteger n) {
		BigInteger a = n, b = m.mod(n), c, d;
		BigInteger i = BigInteger.ZERO, j = BigInteger.ONE;

		while (!b.equals(BigInteger.ZERO)) {
			d = a.divide(b);
			c = a.subtract(d.multiply(b));
			a = b;
			b = c;
			c = i.subtract(d.multiply(j));
			i = j;
			j = c;
		}

		if (i.compareTo(BigInteger.ZERO) < 0) {
			i = i.add(n);
		}

		return i;
	}

	public static BigInteger encrypt(BigInteger n, BigInteger e, BigInteger m) {
		BigInteger c = m.modPow(e, n);
		return c;
	}

	public static BigInteger decrypt(BigInteger n, BigInteger d, BigInteger c) {
		BigInteger m = c.modPow(d, n);
		return m;

	}

	public static BigInteger convertStringToBigInt(String s) {
		String originalMessage = s;
		BigInteger bigInt = new BigInteger(originalMessage.getBytes(StandardCharsets.UTF_8));
		return bigInt;

	}

	public static String convertBigIntToString(BigInteger b) {
		String message = new String(b.toByteArray(), StandardCharsets.UTF_8);
		return message;

	}

	public static double probability(int n) {
		double numerator = Math.log(n) - 2;
		double denominator = Math.log(n) - 2 + 1048577;
		return numerator / denominator;

	}

	public static List<String> splitIntoChunks(String str, int chunkSize) {
		List<String> chunks = new ArrayList<>();
		StringBuilder sb = new StringBuilder(chunkSize);
		int byteCount = 0;

		for (char c : str.toCharArray()) {
			int charByteCount = String.valueOf(c).getBytes(StandardCharsets.UTF_8).length;
			if (byteCount + charByteCount > chunkSize) {
				chunks.add(sb.toString());
				sb = new StringBuilder(chunkSize);
				byteCount = 0;
			}
			sb.append(c);
			byteCount += charByteCount;
		}

		// adding remaining string
		if (sb.length() > 0) {
			chunks.add(sb.toString());
		}

		// join chunks into a single string with a space between each chunk
		return chunks;
	}

}
