export interface UserInfo {
  userId: number;
  nickname: string;
  grade: string;
}

export interface Avatar {
  avatarId: number;
  fileName: string;
  remotePath: string;
}

export interface IDataHeader {
  point: number;
  userInfo: {
    userId: number;
    nickname: string;
    grade: string;
  };
  avatar: {
    avatarId: number | null;
    filename: string | null;
    remotePath: string | null;
  } | null;
}
