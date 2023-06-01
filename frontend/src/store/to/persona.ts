class Persona {
  personaId: string;

  /**
   * Name of the profile
   */
  name: string;

  /**
   * Description of the profile
   */
  description: string;

  imageUrl: string;

  constructor(personaId: string, name: string, description: string, imageUrl: string) {
    this.name = name;
    this.description = description;
    this.personaId = personaId;
    this.imageUrl = imageUrl;
  }
}

export default Persona;
