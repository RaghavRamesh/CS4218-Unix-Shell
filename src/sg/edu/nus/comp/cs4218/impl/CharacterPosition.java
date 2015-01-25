package sg.edu.nus.comp.cs4218.impl;

public class CharacterPosition {
  int mPosition;
  Character mCharacter;
  
  public CharacterPosition(Character character, int position) {
    mPosition = position;
    mCharacter = character;
  }
  
  public int getPosition() {
    return mPosition;
  }

  public Character getCharacter() {
    return mCharacter;
  }
}
