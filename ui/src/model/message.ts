import type { OutComingCommands } from '../ws/action';

export interface IMessage {
  type: OutComingCommands;
  data: any;
}
