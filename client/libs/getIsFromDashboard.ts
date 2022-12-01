export const getIsFromDashboard = (url: string) => {
  const prevPageAndId = url.split('/').slice(-2);
  if (prevPageAndId[0] !== 'dashboard') return false;
  else return prevPageAndId;
};
