import { Base } from './base';
import { CommentResp } from './comment';
export interface Answer extends Base {
  answerId: number;
  authorId: string;
  isLiked: boolean;
  isPicked: boolean;
  answerLikeCount: number;
  commentCount: number;
  commentPreview: CommentResp;
}
