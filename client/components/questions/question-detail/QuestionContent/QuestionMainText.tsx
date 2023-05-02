type MainTextProps = {
  children: string;
};

// 본문 텍스트
export const QuestionMainText = ({ children }: MainTextProps) => {
  return (
    <main className="flex min-h-[20vh]">
      <div
        className="prose max-w-full"
        dangerouslySetInnerHTML={{ __html: children }}
      />
    </main>
  );
};
