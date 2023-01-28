import { Base } from './base';

export interface CommentResp extends Base {
  answerId?: number | null;
  commentId: number;
}
