import { UserInfo } from './user';
import { Avatar } from './user';

export interface CommentResp {
  answerId?: number | null;
  commentId: number;
  articleId: number;
  content: string;
  createdAt: string;
  lastModifiedAt: string;
  userInfo: UserInfo;
  avatar: Avatar;
}
