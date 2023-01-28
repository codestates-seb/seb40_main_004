export interface AuthResp {
  email: string;
  authKey?: string;
}

export type DecodedResp = {
  sub: string;
  id: number;
  nickname: string;
};
