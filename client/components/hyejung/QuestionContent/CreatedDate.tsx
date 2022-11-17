type CreatedDateProps = {
  createdAt: string;
};

export const CreatedDate = ({ createdAt }: CreatedDateProps) => {
  return <time className="text-main-gray text-xs sm:text-sm">{createdAt}</time>;
};
