type TitleProps = {
  title: string;
};

export const QuestionTitle = ({ title }: TitleProps) => {
  return (
    <section className="flex w-full text-2xl sm:text-3xl space-x-2">
      <h1 className="text-main-yellow font-semibold">Q.</h1>
      <h1>{title}</h1>
    </section>
  );
};
