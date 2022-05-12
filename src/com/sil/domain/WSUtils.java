package com.sil.domain;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import com.sil.commonswitch.OtherChannelServiceResponse;
import com.sil.constants.SwiftCoreConstants;

public class WSUtils {
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final Random generator = new Random();
	
	public static String getAccountStatus(int status) {
		String stat = "";
		switch (status) {
		case 1:
		case 2:
		case 12:
		case 15:
		case 7:
		case 9:
		case 10:
		case 13:
		case 14:
		case 16:
		case 8:
		case 11:
		case 17:
		case 18:
		case 19:
		case 20:
			stat = "Active";
			break;
		case 180:
			stat = "NPA";
			break;
		case 3:
			stat = "Closed";
			break;
		case 6:
		case 4:
		case 5:
		case 97:
			stat = "Freeze";
			break;
		case 88:
			stat = "Dormant";
			break;
		default:
			stat = "Unknown";
			break;
		}
		return stat;
	}

	public static String getAccountFreezeType(int status) {
		String type = "";
		switch (status) {
		case 1:
			type = "Partial Freeze";
			break;
		case 2:
			type = "Debit Freeze";
			break;
		case 3:
			type = "Credit Freeze";
			break;
		case 4:
			type = "Full Freeze";
			break;
		default:
			type = "Unknown";
			break;
		}
		return type;

	}



	/**
	 * @description This method used to validate the email address.
	 * @author Sudarshan Maheshwari
	 * @date 18/Jan/2016
	 * @version $Revision$
	 * @param email
	 * @return
	 */
	public static boolean emailValidator(String email) {
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	/**
	 * @description This method used to generates n digit random number and
	 *              return one number from min number to max number.
	 * @author Sudarshan Maheshwari
	 * @date 20/Jan/2016
	 * @version $Revision$
	 * @param minNumber
	 * @param maxNumber
	 * @return
	 */
	public static int generateRandomNumber(int minNumber, int maxNumber) throws Exception {
		generator.setSeed(System.currentTimeMillis());
		int randomNumber = minNumber + generator.nextInt(maxNumber);
		return randomNumber;
	}

	/**
	 * @description Generates a hash for the given plain text value and returns
	 *              a base64-encoded result. Before the hash is computed, a
	 *              random salt is generated and appended to the plain text.
	 *              This salt is stored at the end of the hash value, so it can
	 *              be used later for hash verification.
	 * @author Sudarshan Maheshwari
	 * @date 25/Jan/2016
	 * @version $Revision$
	 * @param plainText
	 *            Plaintext value to be hashed. The function does not check
	 *            whether this parameter is null.
	 * @param hashAlgorithm
	 *            Name of the hash algorithm. Allowed values are: "MD5", "SHA1",
	 *            "SHA256", "SHA384", and "SHA512" (if any other value is
	 *            specified MD5 hashing algorithm will be used). This value is
	 *            case-insensitive.
	 * @param saltBytes
	 *            this parameter can be null, in which case a random salt value
	 *            will be generated.
	 * @return hash value formatted as a base64-encoded String.
	 * @throws NoSuchAlgorithmException 
	 * @throws UnsupportedEncodingException 
	 */	
	public static String computeHash(String plainText, String hashAlgorithm, byte[] saltBytes) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		/** If salt is not specified, generate it on the fly. */
		if (saltBytes == null) {
			/** Define min and max salt sizes. */
			int minSaltSize = 4;
			int maxSaltSize = 8;

			/** Generate a random number for the size of the salt. */
			Random random = new Random();
			int saltSize = random.nextInt(maxSaltSize - minSaltSize +1) + minSaltSize;

			/** Allocate a byte array, which will hold the salt. */
			saltBytes = new byte[saltSize];

			/** Initialize a random number generator. */
			SecureRandom sr;
			sr = SecureRandom.getInstance("SHA1PRNG");
			/** Fill the salt with cryptographically strong byte values. */
			sr.nextBytes(saltBytes);
		}

		/** Convert plain text into a byte array. */
		byte[] plainTextBytes= {};
		plainTextBytes = plainText.getBytes("UTF8");

		/** Allocate array, which will hold plain text and salt. */
		byte[] plainTextWithSaltBytes = new byte[plainTextBytes.length
		                                         + saltBytes.length];

		/** Copy plain text bytes into resulting array. */
		for (int i = 0; i < plainTextBytes.length; i++)
			plainTextWithSaltBytes[i] = plainTextBytes[i];

		/** Append salt bytes to the resulting array. */
		for (int i = 0; i < saltBytes.length; i++)
			plainTextWithSaltBytes[plainTextBytes.length + i] = saltBytes[i];

		/**
		 * Because we support multiple hashing algorithms, we must define
		 * hash object as a common (abstract) base class. We will specify
		 * the actual hashing algorithm class later during object creation.
		 */
		MessageDigest md = null;

		/** Make sure hashing algorithm name is specified. */
		if (hashAlgorithm == null)
			hashAlgorithm = "";
		hashAlgorithm = hashAlgorithm.toUpperCase();
		if(hashAlgorithm.equals("SHA1")) {
			md = MessageDigest.getInstance("SHA-1");
		} else if(hashAlgorithm.equals("SHA256")) {
			md = MessageDigest.getInstance("SHA-256");
		} else if(hashAlgorithm.equals("SHA384")) {
			md = MessageDigest.getInstance("SHA-384");
		} else if(hashAlgorithm.equals("SHA512")) {
			md = MessageDigest.getInstance("SHA-512");
		} else {
			md = MessageDigest.getInstance("MD5");
		}

		md.update(plainTextWithSaltBytes);
		/** Compute hash value of our plain text with appended salt. */
		byte[] hashBytes = md.digest();

		/** Create array which will hold hash and original salt bytes. */
		byte[] hashWithSaltBytes = new byte[hashBytes.length + saltBytes.length];

		/** Copy hash bytes into resulting array. */
		for (int i = 0; i < hashBytes.length; i++)
			hashWithSaltBytes[i] = hashBytes[i];

		/** Append salt bytes to the result. */
		for (int i = 0; i < saltBytes.length; i++)
			hashWithSaltBytes[hashBytes.length + i] = saltBytes[i];

		/** Convert result into a base64-encoded String. */		
		String hashValue = new String(Base64.encodeBase64(hashWithSaltBytes));

		/** Return the result. */
		return hashValue;//StringUtils.isNotBlank(hashValue) ? hashValue : null;
	}

	/**
	 * @description Compares a hash of the specified plain text value to a given
	 *              hash value. Plain text is hashed with the same salt value as
	 *              the original hash.
	 * @author Sudarshan Maheshwari
	 * @date 25/Jan/2016
	 * @version $Revision$
	 * @param plainText
	 *            Plain text to be verified against the specified hash. The
	 *            function does not check whether this parameter is null.
	 * @param hashAlgorithm
	 *            Name of the hash algorithm. Allowed values are: "MD5", "SHA1",
	 *            "SHA256", "SHA384", and "SHA512" (if any other value is
	 *            specified, MD5 hashing algorithm will be used). This value is
	 *            case-insensitive.
	 * @param hashValue
	 *            Base64-encoded hash value produced by ComputeHash function.
	 *            This value includes the original salt appended to it.
	 * @return If computed hash mathes the specified hash the function the
	 *         return value is true; otherwise, the function returns false.
	 * @throws UnsupportedEncodingException 
	 * @throws NoSuchAlgorithmException 
	 */	
	public static boolean verifyHash(String plainText, String hashAlgorithm, String hashValue) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		/** Convert base64-encoded hash value into a byte array. */	
		byte[] hashWithSaltBytes = {};		
		hashWithSaltBytes = Base64.decodeBase64(hashValue.getBytes());

		/** We must know size of hash (without salt). */
		int hashSizeInBits, hashSizeInBytes;

		/** Make sure that hashing algorithm name is specified. */
		if (hashAlgorithm == null)
			hashAlgorithm = "";		

		hashAlgorithm = hashAlgorithm.toUpperCase();

		/** Size of hash is based on the specified algorithm. */
		if(hashAlgorithm.equals("SHA1")) {
			hashSizeInBits = 160;
		} else if(hashAlgorithm.equals("SHA256")) {
			hashSizeInBits = 256;
		} else if(hashAlgorithm.equals("SHA384")) {
			hashSizeInBits = 384;
		} else if(hashAlgorithm.equals("SHA512")) {
			hashSizeInBits = 512;
		} else {
			hashSizeInBits = 128;
		}

		/** Convert size of hash from bits to bytes. */
		hashSizeInBytes = hashSizeInBits / 8;

		/** Make sure that the specified hash value is long enough. */
		if (hashWithSaltBytes.length < hashSizeInBytes)
			return false;

		/** Allocate array to hold original salt bytes retrieved from hash. */
		byte[] saltBytes = new byte[hashWithSaltBytes.length - hashSizeInBytes];

		/** Copy salt from the end of the hash to the new array. */
		for (int i = 0; i < saltBytes.length; i++)
			saltBytes[i] = hashWithSaltBytes[hashSizeInBytes + i];

		/** Compute a new hash String. */
		String expectedHashString = computeHash(plainText, hashAlgorithm, saltBytes);

		/** If the computed hash matches the specified hash, the plain text value must be correct. */
		return (hashValue.equals(expectedHashString));
	}

	/**
	 * @description This method used to validate special characters except
	 *              whitespace( ), dot(.), hyphen(-) and colon(:).
	 * @author Sudarshan Maheshwari
	 * @date 05/Feb/2016
	 * @version $Revision$
	 * @param name
	 * @return
	 */
	public static boolean isValidSpecialChar(String name) {
		String regx = "^[\\p{L} .-:]+$";
		Pattern pattern = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(name);
		return matcher.find();
	}


	public static String checkSpecialChar(String value) {
		String regx = "[^a-zA-Z .\\-:]";
		value = value.replaceAll(regx, " ");
		return value;
	}

	public static void main(String[] args) {
		try {
			System.out.println(WSUtils.verifyHash("sil@123", "SHA1", "D+LHzUEfMrJLLqjZnJ7m1jhAVPab/5Je0FI="));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static OtherChannelServiceResponse getWSReturnOutput(String response, String errorMessage, String errorCode, String custNo, Long lbrCode) {
		OtherChannelServiceResponse othChannelSerRes = new OtherChannelServiceResponse();
		othChannelSerRes.setResponse(response);
		othChannelSerRes.setErrorMessage(errorMessage);
		othChannelSerRes.setErrorCode(errorCode);
		othChannelSerRes.setCustNo(StringUtils.isBlank(custNo) ? SwiftCoreConstants.NOT_USED_PROCESS : custNo);
		othChannelSerRes.setTransactionId(lbrCode+"");
		return othChannelSerRes;
	}
	
	public static OtherChannelServiceResponse getWSReturnOutput(String response, String errorMessage, String errorCode, String custNo, String mobileNo) {
		OtherChannelServiceResponse othChannelSerRes = new OtherChannelServiceResponse();
		othChannelSerRes.setResponse(response);
		othChannelSerRes.setErrorMessage(errorMessage);
		othChannelSerRes.setErrorCode(errorCode);
		othChannelSerRes.setCustNo(custNo);
		othChannelSerRes.setTransactionId(mobileNo);
		return othChannelSerRes;
	}
	
	/**
	 * @description This method used to set the response/message/code in
	 *              IMPSTransactionResponse object.
	 * @author Sudarshan Maheshwari
	 * @date 11/Dec/2015
	 * @version $Revision$
	 * @param response
	 * @param errorMessage
	 * @param errorCode
	 * @param rrnNo
	 * @param stan
	 * @param nickNameDebit
	 * @return
	 */
	public static IMPSTransactionResponse getReturnWSTxOutput(String response, String errorMessage, String errorCode,
			String rrnNo, String stan, String nickNameDebit, String custNo, Long lbrCode) {
		IMPSTransactionResponse transRes = new IMPSTransactionResponse();
		transRes.setResponse(errorMessage);
		//transRes.set
		transRes.setErrorMessage(response);   //change By Manish
		transRes.setErrorCode(errorCode);
		transRes.setRrnNo(rrnNo);
		transRes.setStan(stan);
		transRes.setNickNameDebit(nickNameDebit);
		transRes.setCustNo(StringUtils.isBlank(custNo) ? SwiftCoreConstants.NOT_USED_PROCESS : custNo);
		transRes.setLbrCode(lbrCode);
		return transRes;
	}
}
