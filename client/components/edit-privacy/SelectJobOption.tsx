type OptionType = {
  value: string;
  title: string;
};

type Props = {
  options: OptionType[];
};

const SelectJobOption = ({ options }: Props) => {
  return (
    <>
      {options.map((option) => (
        <option key={option.value} value={option.value}>
          {option.title}
        </option>
      ))}
    </>
  );
};

export default SelectJobOption;
