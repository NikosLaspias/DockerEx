//ContainerCreationTest: a class that executes the class ContainerCreation
//Copyright(C) 2023/24 Eleutheria Koutsiouri
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program. If not, see <https://www.gnu.org/licenses/>.

package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ContainerCreationTest {

    @Test
    public void testUserInputValidationForMultipleContainers() {
        String[] containerIds = {
                "05007dd4598a0c01f23275e56bf1848cfc4f2b17426f7bebf1a6219ecda91a41",
                "845a8838d59466c8ac7d89ecf77c7a5fd348492f9c14e51df8a7942660c7aca2",
                "2a13ec3d58d1308e9936803a673265d2440b6fb1465830e5bbab1fc2f4d6a60c",
                "420311d9b406c0a649c77e685df99fd535d087492a217e95327751d0167f07d0"
        };

        for (String containerId : containerIds) {
            TestUserInputProvider testUserInputProvider = new TestUserInputProvider();
            testUserInputProvider.setSimulatedInput(containerId);
            String userInput = testUserInputProvider.promptUserForContainerId();
            assertNotNull(userInput);
        }
    }
}
