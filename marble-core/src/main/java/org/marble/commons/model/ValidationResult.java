package org.marble.commons.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidationResult {

	public static final Integer POSITIVE = 1;
	public static final Integer NEUTRAL = 0;
	public static final Integer NEGATIVE = -1;

	private Integer totalItems = 0;
	private Integer totalOriginalPositiveItems = 0;
	private Integer totalOriginalNeutralItems = 0;
	private Integer totalOriginalNegativeItems = 0;
	private Integer totalProcessedPositiveItems = 0;
	private Integer totalProcessedNeutralItems = 0;
	private Integer totalProcessedNegativeItems = 0;
	private Integer totalPositiveSuccesses = 0;
	private Integer totalNeutralSuccesses = 0;
	private Integer totalNegativeSuccesses = 0;

	public void addResult(Integer expected, Integer obtained) {
		this.totalItems++;
		if (expected.equals(ValidationResult.POSITIVE)) {
			this.totalOriginalPositiveItems++;
		} else if (expected.equals(ValidationResult.NEUTRAL)) {
			this.totalOriginalNeutralItems++;
		} else if (expected.equals(ValidationResult.NEGATIVE)) {
			this.totalOriginalNegativeItems++;
		}

		if (obtained.equals(ValidationResult.POSITIVE)) {
			this.totalProcessedPositiveItems++;
		} else if (obtained.equals(ValidationResult.NEUTRAL)) {
			this.totalProcessedNeutralItems++;
		} else if (obtained.equals(ValidationResult.NEGATIVE)) {
			this.totalProcessedNegativeItems++;
		}

		if (expected.equals(obtained)) {
			if (obtained.equals(ValidationResult.POSITIVE)) {
				this.totalPositiveSuccesses++;
			} else if (obtained.equals(ValidationResult.NEUTRAL)) {
				this.totalNeutralSuccesses++;
			} else if (obtained.equals(ValidationResult.NEGATIVE)) {
				this.totalNegativeSuccesses++;
			}
		}
	}

	public void addPositiveResult(Integer expected) {
		this.addResult(expected, ValidationResult.POSITIVE);
	}

	public void addNeutralResult(Integer expected) {
		this.addResult(expected, ValidationResult.NEUTRAL);
	}

	public void addNegativeResult(Integer expected) {
		this.addResult(expected, ValidationResult.NEGATIVE);
	}

	public String getResults() {
		String results = "Validation Results:\n";
		results += "Total items processed:...................<" + this.totalItems + ">\n";
		results += "Total items with positive polarity:......<" + this.totalOriginalPositiveItems + ">\n";
		results += "Total items found to be positive:........<" + this.totalProcessedPositiveItems + ">\n";
		results += "Percentage of success in positives:......<"
				+ (((float) this.totalPositiveSuccesses / (float) this.totalOriginalPositiveItems) * 100F) + "%>\n";

		results += "Total items with neutral polarity:.......<" + this.totalOriginalNeutralItems + ">\n";
		results += "Total items found to be neutral:.........<" + this.totalProcessedNeutralItems + ">\n";
		results += "Percentage of success in neutrals:.......<"
				+ (((float) this.totalNeutralSuccesses / (float) this.totalOriginalNeutralItems) * 100F) + "%>\n";

		results += "Total items with negative polarity:......<" + this.totalOriginalNegativeItems + ">\n";
		results += "Total items found to be negative:........<" + this.totalProcessedNegativeItems + ">\n";
		results += "Percentage of success in negatives:......<"
				+ (((float) this.totalNegativeSuccesses / (float) this.totalOriginalNegativeItems) * 100F) + "%>\n";

		results += "Percentage of success of the processor:..<"
				+ (((float) (this.totalPositiveSuccesses + this.totalNeutralSuccesses + this.totalNegativeSuccesses) / (float) this.totalItems) * 100F)
				+ "%>\n";
		results += ".........................Recall (+):.....<" + ((float) this.totalPositiveSuccesses / (float) this.totalOriginalPositiveItems) + ">\n";
		results += ".........................Recall (/):.....<" + ((float) this.totalNeutralSuccesses / (float) this.totalOriginalNeutralItems) + ">\n";
		results += ".........................Recall (-):.....<" + ((float) this.totalNegativeSuccesses / (float) this.totalOriginalNegativeItems) + ">\n";

		//results += ".........................Recall:.........<"
		//		+ ((float) (this.totalPositiveSuccesses + this.totalNeutralSuccesses + this.totalNegativeSuccesses) / (float) (this.totalItems));

		results += ".........................Precision (+):..<" + ((float) this.totalPositiveSuccesses / (float) this.totalProcessedPositiveItems) + ">\n";
		results += ".........................Precision (/):..<" + ((float) this.totalNeutralSuccesses / (float) this.totalProcessedNeutralItems) + ">\n";
		results += ".........................Precision (-):..<" + ((float) this.totalNegativeSuccesses / (float) this.totalProcessedNegativeItems) + ">\n";
		
		//results += ".........................Recall:.........<"
		//		+ ((float) (this.totalPositiveSuccesses + this.totalNeutralSuccesses + this.totalNegativeSuccesses) / (float) (this.totalItems));
		
		return results;

	}
}
