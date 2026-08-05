package org.azzadal.main.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import lombok.Getter;
import org.azzadal.main.enumeration.Value;

/**
 * Игровое поле.
 */
public class Field implements Serializable {

  @Serial
  private static final long serialVersionUID = 1;

  @Getter
  private final Value[][] field = new Value[3][3];

  public void insert(Position position, Value value) {
    field[position.x()][position.y()] = value;
  }

  public boolean isWin(Position position) {
    int x = position.x();
    int y = position.y();

    boolean b = checkStr(x) || checkCol(y);
    // check diagonal
    if (
      (x == 0 && (y == 0 || y == 2)) ||
      (x == 1 && y == 1) ||
      ((x == 2 && (y == 0 || y == 2)))
    ) {
      b = b || checkDiagonal();
    }
    return b;
  }

  private boolean checkStr(int strNum) {
    Value f = field[strNum][0];
    Value s = field[strNum][1];
    Value t = field[strNum][2];
    return f != null && f.equals(s) && f.equals(t);
  }

  private boolean checkCol(int colNum) {
    Value f = field[0][colNum];
    Value s = field[1][colNum];
    Value t = field[2][colNum];
    return f != null && f.equals(s) && f.equals(t);
  }

  private boolean checkDiagonal() {
    Value fm = field[0][0];
    Value sm = field[1][1];
    Value tm = field[2][2];
    boolean main = fm != null && fm.equals(sm) && fm.equals(tm);

    Value fs = field[0][2];
    Value ss = field[1][1];
    Value ts = field[2][0];
    boolean side = fs != null && fs.equals(ss) && fs.equals(ts);

    return main || side;
  }

  @Override
  public String toString() {
    return "Field{" + "field=" + Arrays.deepToString(field) + '}';
  }
}
