import {LorcanaCardDTO, LorcanaSetDTO} from "@/types";

export type EditedLorcanaCard = Omit<LorcanaCardDTO, 'setIds'> & {
    set: LorcanaSetDTO,
};
