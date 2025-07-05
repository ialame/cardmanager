import {LorcanaSerieDTO, LorcanaSetDTO} from "@/types";

export type EditedLorcanaSet = Omit<LorcanaSetDTO, 'serieId'> & {
    serie: LorcanaSerieDTO,
};
