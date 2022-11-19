import { useEffect, useState } from 'react';

export type SelectOption = {
  label: string;
  values: string | number;
};

type MultipleSelectProps = {
  multiple: true;
  values: SelectOption[];
  onChange: (value: SelectOption[]) => void;
};

type SingleSelectProps = {
  multiple?: false;
  values?: SelectOption;
  onChange: (value: SelectOption | undefined) => void;
};

type SelectProps = {
  options: SelectOption[];
} & (SingleSelectProps | MultipleSelectProps);

export const Select = ({
  multiple,
  values,
  onChange,
  options,
}: SelectProps) => {
  const [isOpen, setIsOpen] = useState(true);
  const [highlightedIndex, setHighlightIndex] = useState(0);

  const clearOptions = () => {
    multiple ? onChange([]) : onChange(undefined);
  };

  const selectOption = (option: SelectOption) => {
    if (multiple) {
      if (values?.includes(option))
        onChange(values.filter((o) => o !== option));
      else onChange([...values, option]);
    } else {
      if (option !== values) onChange(option);
    }
  };

  const isOptionSelected = (option: SelectOption) => {
    return multiple ? values.includes(option) : option === values;
  };

  useEffect(() => {
    if (isOpen) setHighlightIndex(0);
  }, [isOpen]);

  return (
    <div
      onBlur={() => setIsOpen(false)}
      onClick={() => setIsOpen((prev) => !prev)}
      tabIndex={0}
      className="relative w-80 min-h-[1.5em] flex justify-start ml-10 border-[0.05em] border-solid border-slate-800 gap-1 rounded-md outline-none cursor-pointer hover:border-main-orange"
    >
      <span className="flex-grow flex gap-2 flex-wrap ">
        {multiple
          ? values.map((value) => (
              <button
                key={value.values}
                onClick={(e) => {
                  e.stopPropagation();
                  selectOption(value);
                }}
                className="rounded-md flex items-center border-[0.15em] border-solid border-main-gray rouned-sm my-2 py-1 px-1 mx-1 gap-1 cursor-pointer bg-transparent outline-none hover:bg-main-yellow hover:border-main-gray focus:bg-main-yellow focus:border-main-yellow"
              >
                {value.label}
                <span className="hover:text-main-gray">&times;</span>
              </button>
            ))
          : values?.label}
      </span>
      <button
        onClick={(event) => {
          event.stopPropagation();
          clearOptions();
        }}
        className="bg-none text-slate-800 border-none outline-none cursor-pointer p-2 text-lg hover:text-slate-500 focus:text-slate-500"
      >
        &times;
      </button>
      {/* <div className="bg-slate-800 self-stretch w-[0.05em]"></div> */}
      {/* <div className="-translate-x-0.5 translate-y-3.5 border-t-slate-800 border-transparent border-solid border-[0.25em]"></div> */}
      <ul
        className={`absolute m-0 p-0 list-none max-h-[15em] overflow-y-auto border-solid border-[0.05rem] border-slate-800 rounded-lg w-full left-0 top-[calc(100%+0.25em)] bg-white z-[100] px-2 py-1 cursor-pointer block ${
          isOpen
            ? 'absolute m-0 p-0 list-none max-h-[15em] overflow-y-auto border-solid border-[0.05rem] border-slate-800 rounded-sm w-full left-0 top-[calc(100%+0.25em)] bg-white z-[100] px-2 py-1 cursor-pointer hidden'
            : ''
        }`}
      >
        {options.map((option, index) => (
          <li
            onClick={(event) => {
              event.stopPropagation();
              selectOption(option);
              setIsOpen(false);
            }}
            key={option.values}
            onMouseEnter={() => setHighlightIndex(index)}
            className={`px-2 py-1 cursor-pointer ${
              isOptionSelected(option) ? 'bg-main-yellow' : ''
            } ${index === highlightedIndex ? 'bg-main-orange text-white' : ''}`}
          >
            {option.label}
          </li>
        ))}
      </ul>
    </div>
  );
};
