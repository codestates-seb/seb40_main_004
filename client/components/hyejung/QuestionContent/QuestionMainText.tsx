type MainTextProps = {
  content: string;
};

export const QuestionMainText = ({ content }: MainTextProps) => {
  return (
    <main>
      <p>{content}</p>
    </main>
  );
};
