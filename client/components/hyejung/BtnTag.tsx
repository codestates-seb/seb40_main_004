type ButtonText = { children: string };

export const TagButton = ({ children }: ButtonText) => {
  return (
    <button className="bg-main-yellow text-xs sm:text-sm font-semibold px-5 py-1 rounded-full">
      {children}
    </button>
  );
};
