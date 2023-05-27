class MapUtil {
  static mapToJsonString(map: any) {
    let json: any = {};
    map.forEach((value: any, key: string) => {
      json[key] = value;
    });
    return JSON.stringify(json);
  }

  static jsonStringToMap(jsonStr: string) {
    const jsonObj = JSON.parse(jsonStr);
    const map = new Map();

    Object.entries(jsonObj).forEach(([key, value]) => {
      map.set(key, value);
    });

    return map;
  }

}

export default MapUtil;