class DateUtil {
  static getNow(): Date {
    return new Date();
  }

  static isValidDate(dateStr: string): boolean {
    return !isNaN(Date.parse(dateStr));
  }

  static formatDate(date: Date): string {
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0'); // Months are zero-based, so we need to add 1
    const year = date.getFullYear();

    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const seconds = date.getSeconds().toString().padStart(2, '0');

//    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    return `${month}/${day} ${hours}:${minutes}`;
  }
}

export default DateUtil;