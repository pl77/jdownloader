define("coreCrypto", function () {
	/**
	 * Extend CryptoJS with function to split WordArray
	 */
	CryptoJS.lib.WordArray.firstHalf = function () {
		if (!this._firstHalf) {
			this._firstHalf = new CryptoJS.lib.WordArray.init(this.words.slice(0, this.words.length / 2));
		}
		return this._firstHalf;
	};
	CryptoJS.lib.WordArray.secondHalf = function () {
		if (!this._secondHalf) this._secondHalf = new CryptoJS.lib.WordArray.init(this.words
			.slice(this.words.length / 2, this.words.length));
		return this._secondHalf;
	};
	return CryptoJS;
});