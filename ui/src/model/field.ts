export type Position = {
  x: number;
  y: number;
};

export type Value = 'X' | 'O' | null;

export class GameField {
  private field: Value[][];
  private blocking: boolean = false;

  constructor() {
    this.field = Array(3)
      .fill(null)
      .map(() => Array(3).fill(null));
  }

  get isBlocking() {
    return this.blocking;
  }

  public set block(value: boolean) {
    this.blocking = value;
  }

  public getField() {
    return this.field;
  }

  public setValue(position: Position, value: Value) {
    this.field[position.x][position.y] = value;
  }

  public setField(field: Value[][]) {
    console.log('кал', field);

    this.field = field;
  }
}
