import { Tags } from './article';
import { Avatar, UserInfo } from './user';

export interface Base {
  articleId: number;
  category?: string;
  title: string;
  content: string;
  clicks?: number;
  likes?: number;
  isClosed?: boolean;
  createdAt: string;
  lastModifiedAt: string;
  tags?: Tags[];
  avatar: Avatar;
  userInfo: UserInfo;
}
