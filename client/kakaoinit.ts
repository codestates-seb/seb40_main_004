export const KakaoInit = () => {
  const kakao = (window as any).Kakao;
  if (!kakao.isInitialized()) kakao.init(process.env.NEXT_PUBLIC_KAKAO);
  // console.log(kakao.isInitialized());

  return kakao;
};
