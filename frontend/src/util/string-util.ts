class StringUtil {

  static truncateString(str: string, maxLength: number, suffix = '...') {
    if (typeof str !== 'string' || typeof maxLength !== 'number') {
      throw new Error('Invalid input. Expected a string and a number.');
    }

    if (str.length > maxLength) {
      return str.substring(0, maxLength) + suffix;
    } else {
      return str;
    }
  }

}

export default StringUtil;