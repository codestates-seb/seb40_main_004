import { CommentResp } from './comment';
import { Avatar } from './user';
import { UserInfo } from './user';

export interface Answer {
  answerId: number;
  authorId: string;
  content: string;
  createdAt: string;
  isLiked: boolean;
  isPicked: boolean;
  answerLikeCount: number;
  commentCount: number;
  commentPreview: CommentResp;
  avatar: Avatar;
  userInfo: UserInfo;
}
