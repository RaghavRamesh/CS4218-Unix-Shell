package sg.edu.nus.comp.cs4218.impl;

public class CharacterPosition {
  int position;
  Character character;
  
  public CharacterPosition(Character _char, int _pos) {
    character = _char;
    position = _pos;
  }
  
  public int getPosition() {
    return position;
  }

  public Character getCharacter() {
    return character;
  }
}
