export const elapsedTime = (pastDate: string) => {
  const now = new Date();
  const past = new Date(pastDate);
  const diff = now.getTime() - past.getTime();
  const sec = Math.floor(diff / 1000) + 1;
  const min = Math.floor(sec / 60);
  const hour = Math.floor(min / 60);
  const day = Math.floor(hour / 24);
  const month = Math.floor(day / 30);
  const year = Math.floor(month / 12);

  if (year) return `${year}년 전`;
  if (month) return `${month}달 전`;
  if (day) return `${day}일 전`;
  if (hour) return `${hour}시간 전`;
  if (min) return `${min}분 전`;
  if (sec) return `${sec}초 전`;
};
