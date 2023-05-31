class Persona {
  id: string;

  /**
   * Name of the profile
   */
  name: string;

  /**
   * Description of the profile
   */
  description: string;

  imageUrl: string;

  constructor(id: string, name: string, description: string, imageUrl: string) {
    this.name = name;
    this.description = description;
    this.id = id;
    this.imageUrl = imageUrl;
  }
}

export default Persona;
